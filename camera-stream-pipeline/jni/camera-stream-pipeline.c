#include <string.h>
#include <jni.h>
#include <android/log.h>
#include <gst/gst.h>
#include <pthread.h>
#include "gstwebrtcfeedbackbin.h"
#include "camerasrcbin.h"
#include "gstlivepublisherbin.h"

GST_DEBUG_CATEGORY_STATIC (debug_category);
#define GST_CAT_DEFAULT debug_category

/**
 * These macros provide a way to store the native pointer to CustomData, which might be 32 or 64 bits, into
 * a jlong, which is always 64 bits, without warnings.
 */
#if GLIB_SIZEOF_VOID_P == 8
# define GET_CUSTOM_DATA(env, thiz, fieldID) (CustomData *)(*env)->GetLongField (env, thiz, fieldID)
# define SET_CUSTOM_DATA(env, thiz, fieldID, data) (*env)->SetLongField (env, thiz, fieldID, (jlong)data)
#else
# define GET_CUSTOM_DATA(env, thiz, fieldID) (CustomData *)(jint)(*env)->GetLongField (env, thiz, fieldID)
# define SET_CUSTOM_DATA(env, thiz, fieldID, data) (*env)->SetLongField (env, thiz, fieldID, (jlong)(jint)data)
#endif


/** Structure to contain all our information, so we can pass it to callbacks */
typedef struct _CustomData {
    jobject app;                  /* Application instance, used to call its methods. A global reference is kept. */
    GstElement *pipeline;         /* The running pipeline */
    GstElement *webrtc_feedback_bin;
    GstElement *live_publisher_bin;
    GMainContext *context;        /* GLib context used to run the main loop */
    GMainLoop *main_loop;         /* GLib main loop */
    gboolean initialized;         /* To avoid informing the UI multiple times about the initialization */
    gchar *rtsp_location;
} CustomData;

/** These global variables cache values which are not changing during execution */
static pthread_t gst_app_thread;
static pthread_key_t current_jni_env;
static JavaVM *java_vm;
static jfieldID custom_data_field_id;
static jmethodID on_ice_candidate_method_id;
static jmethodID on_sdp_created_method_id;
static jmethodID on_gstreamer_initialized_method_id;

extern guint gst_webrtc_feedback_bin_signals[LAST_WEBRTC_FEEDBACK_SIGNAL];
extern guint gst_live_publisher_bin_signals[LAST_LIVE_PUBLISHER_SIGNAL];

static const gchar *launch_string_template = "rtspsrc location=%s latency=0 ! rtpjitterbuffer mode=0 ! rtph264depay ! h264parse"
                                             "! tee name=v_tee ! webrtcfeedbackbin name=webrtc_feedback_bin"
                                             "  "
                                             "v_tee. ! livepublisherbin name=live_publisher_bin";

static void
on_sdp_created_handler(G_GNUC_UNUSED GstElement *webrtcfeedbackbin, gint id, gchar *type,
                       gchar *sdp, gpointer user_data);

static void
on_ice_candidate_handler(G_GNUC_UNUSED GstElement *webrtcfeedbackbin,
                         gint id, gint m_line_index, gchar *candidate, gpointer user_data);

/**
 * Private methods
 */

/** Register this thread with the VM */
static JNIEnv *
attach_current_thread(void) {
    JNIEnv *env;
    JavaVMAttachArgs args;

    GST_DEBUG ("Attaching thread %p", g_thread_self());
    args.version = JNI_VERSION_1_4;
    args.name = NULL;
    args.group = NULL;

    if ((*java_vm)->AttachCurrentThread(java_vm, &env, &args) < 0) {
        GST_ERROR ("Failed to attach current thread");
        return NULL;
    }

    return env;
}

/** Unregister this thread from the VM */
static void
detach_current_thread(void *env) {
    GST_DEBUG ("Detaching thread %p", g_thread_self());
    (*java_vm)->DetachCurrentThread(java_vm);
}

/** Retrieve the JNI environment for this thread */
static JNIEnv *
get_jni_env(void) {
    JNIEnv *env;

    if ((env = pthread_getspecific(current_jni_env)) == NULL) {
        env = attach_current_thread();
        pthread_setspecific(current_jni_env, env);
    }

    return env;
}

/** Retrieve errors from the bus and show them on the UI */
static void
error_cb(GstBus *bus, GstMessage *msg, CustomData *data) {
    GError *err;
    gchar *debug_info;
    gchar *message_string;

    gst_message_parse_error(msg, &err, &debug_info);
    message_string =
            g_strdup_printf("Error received from element %s: %s",
                            GST_OBJECT_NAME (msg->src), err->message);
    g_clear_error(&err);
    g_free(debug_info);
    g_free(message_string);
    gst_element_set_state(data->pipeline, GST_STATE_NULL);
}

/** Notify UI about pipeline state changes */
static void
state_changed_cb(GstBus *bus, GstMessage *msg, CustomData *data) {
    GstState old_state, new_state, pending_state;
    gst_message_parse_state_changed(msg, &old_state, &new_state, &pending_state);
    /** Only pay attention to messages coming from the pipeline, not its children */
    if (GST_MESSAGE_SRC (msg) == GST_OBJECT (data->pipeline)) {
        gchar *message = g_strdup_printf("State changed to %s",
                                         gst_element_state_get_name(new_state));
        g_free(message);
    }
}

/**
 * Check if all conditions are met to report GStreamer as initialized.
 * These conditions will change depending on the application
 */
static void
check_initialization_complete(CustomData *data) {
    JNIEnv *env = get_jni_env();
    if (!data->initialized && data->main_loop) {
        GST_DEBUG ("Initialization complete, notifying application. main_loop:%p",
                   data->main_loop);
        (*env)->CallVoidMethod(env, data->app, on_gstreamer_initialized_method_id);
        if ((*env)->ExceptionCheck(env)) {
            GST_ERROR ("Failed to call Java method");
            (*env)->ExceptionClear(env);
        }
        data->initialized = TRUE;
    }
}

/** Main method for the native code. This is executed on its own thread. */
static void *
app_function(void *userdata) {
    JavaVMAttachArgs args;
    GstBus *bus;
    CustomData *data = (CustomData *) userdata;
    GSource *bus_source;
    GError *error = NULL;

    webrtc_feedback_bin_init(NULL);
    live_publisher_bin_init(NULL);

    GST_DEBUG ("Creating pipeline in CustomData at %p", data);

    /** Create our own GLib Main Context and make it the default one */
    data->context = g_main_context_new();
    g_main_context_push_thread_default(data->context);


    /** Build pipeline */
    gchar *launch_string = g_strdup_printf(launch_string_template,
                                           data->rtsp_location);
    data->pipeline = gst_parse_launch(launch_string, &error);
    if (error) {
        gchar *message =
                g_strdup_printf("Unable to build pipeline: %s", error->message);
        g_clear_error(&error);
        g_free(message);
        return NULL;
    }
    data->webrtc_feedback_bin = gst_bin_get_by_name(GST_BIN (data->pipeline),
                                                    "webrtc_feedback_bin");
    g_signal_connect (data->webrtc_feedback_bin, "on-sdp-created",
                      G_CALLBACK(on_sdp_created_handler), (gpointer) data);

    g_signal_connect (data->webrtc_feedback_bin, "on-ice-candidate",
                      G_CALLBACK(on_ice_candidate_handler), (gpointer) data);

    data->live_publisher_bin = gst_bin_get_by_name(GST_BIN (data->pipeline),
                                                   "live_publisher_bin");

    /** Instruct the bus to emit signals for each received message, and connect to the interesting signals */
    bus = gst_element_get_bus(data->pipeline);
    bus_source = gst_bus_create_watch(bus);
    g_source_set_callback(bus_source, (GSourceFunc) gst_bus_async_signal_func,
                          NULL, NULL);
    g_source_attach(bus_source, data->context);
    g_source_unref(bus_source);
    g_signal_connect (G_OBJECT(bus), "message::error", (GCallback) error_cb,
                      data);
    g_signal_connect (G_OBJECT(bus), "message::state-changed",
                      (GCallback) state_changed_cb, data);
    gst_object_unref(bus);

    /** Create a GLib Main Loop and set it to run */
    GST_DEBUG ("Entering main loop... (CustomData:%p)", data);
    data->main_loop = g_main_loop_new(data->context, FALSE);
    check_initialization_complete(data);
    g_main_loop_run(data->main_loop);
    GST_DEBUG ("Exited main loop");
    g_main_loop_unref(data->main_loop);
    data->main_loop = NULL;

    /** Free resources */
    g_main_context_pop_thread_default(data->context);
    g_main_context_unref(data->context);
    gst_element_set_state(data->pipeline, GST_STATE_NULL);
    gst_object_unref(data->pipeline);

    return NULL;
}

/** SDP-related event: called on success of Create{Offer,Answer}(). */
static void
on_sdp_created_handler(G_GNUC_UNUSED GstElement *webrtcfeedbackbin, gint id, gchar *type,
                       gchar *sdp, gpointer user_data) {
    CustomData *data = (CustomData *) user_data;
    JNIEnv *env = get_jni_env();
    jstring j_sdp = (*env)->NewStringUTF(env, sdp);
    jstring j_type = (*env)->NewStringUTF(env, type);
    (*env)->CallVoidMethod(env, data->app, on_sdp_created_method_id, id, j_type, j_sdp);
    if ((*env)->ExceptionCheck(env)) {
        GST_ERROR ("Failed to call Java method");
        (*env)->ExceptionClear(env);
    }
    (*env)->DeleteLocalRef(env, j_sdp);
    (*env)->DeleteLocalRef(env, j_type);

}

/**
 * Ice-related event: called when an ice candidate is generated,
 * The candidate should be transmitted to the remote peer over the signaling channel
 * so the remote peer can add it to its set of remote candidates.
 */
static void
on_ice_candidate_handler(G_GNUC_UNUSED GstElement *webrtcfeedbackbin,
                         gint id, gint m_line_index, gchar *candidate, gpointer user_data) {

    CustomData *data = (CustomData *) user_data;
    JNIEnv *env = get_jni_env();
    jstring j_candidate = (*env)->NewStringUTF(env, candidate);
    (*env)->CallVoidMethod(env, data->app, on_ice_candidate_method_id, id, m_line_index,
                           j_candidate);
    if ((*env)->ExceptionCheck(env)) {
        GST_ERROR ("Failed to call Java method");
        (*env)->ExceptionClear(env);
    }
    (*env)->DeleteLocalRef(env, j_candidate);
}

/**
 * Java Bindings
 */

/** Instruct the native code to create its internal data structure, pipeline and thread */
static void
gst_native_init(JNIEnv *env, jobject thiz, jstring j_rtsp_location) {
    CustomData *data = g_new0 (CustomData, 1);
    SET_CUSTOM_DATA (env, thiz, custom_data_field_id, data);
    GST_DEBUG_CATEGORY_INIT (debug_category, "camera-stream-pipeline", 0,
                             "Camera stream pipeline.");
    gst_debug_set_threshold_for_name("camera-stream-pipeline", GST_LEVEL_DEBUG);
    gst_debug_set_threshold_for_name("webrtc*", GST_LEVEL_INFO);
    gst_debug_set_threshold_for_name("rtmp*", GST_LEVEL_DEBUG);
    GST_DEBUG ("Created CustomData at %p", data);
    data->app = (*env)->NewGlobalRef(env, thiz);
    data->rtsp_location = (*env)->GetStringUTFChars(env, j_rtsp_location, 0);
    GST_DEBUG ("Created GlobalRef for app object at %p", data->app);
    pthread_create(&gst_app_thread, NULL, &app_function, data);

}

/** Quit the main loop, remove the native thread and free resources */
static void
gst_native_finalize(JNIEnv *env, jobject thiz) {
    CustomData *data = GET_CUSTOM_DATA (env, thiz, custom_data_field_id);
    if (!data)
        return;
    if (!data->pipeline) {
        GST_ERROR("pipelinen is null while changing its state to NULL");
        return;
    }

    GST_DEBUG ("Quitting main loop...");
    g_main_loop_quit(data->main_loop);
    GST_DEBUG ("Waiting for thread to finish...");
    pthread_join(gst_app_thread, NULL);
    GST_DEBUG ("Deleting GlobalRef for app object at %p", data->app);
    (*env)->DeleteGlobalRef(env, data->app);
    GST_DEBUG ("Freeing CustomData at %p", data);
    g_free(data);
    SET_CUSTOM_DATA (env, thiz, custom_data_field_id, NULL);
    GST_DEBUG ("Done finalizing");
}

/** Set pipeline to PLAYING state */
static void
gst_native_play(JNIEnv *env, jobject thiz) {
    CustomData *data = GET_CUSTOM_DATA (env, thiz, custom_data_field_id);
    if (!data)
        return;
    GST_DEBUG ("Setting state to PLAYING");
    gst_element_set_state(data->pipeline, GST_STATE_PLAYING);
}

/** Set pipeline to PAUSED state */
static void
gst_native_pause(JNIEnv *env, jobject thiz) {
    CustomData *data = GET_CUSTOM_DATA (env, thiz, custom_data_field_id);
    if (!data)
        return;
    GST_DEBUG ("Setting state to PAUSED");
    gst_element_set_state(data->pipeline, GST_STATE_PAUSED);
}

/** Static class initializer: retrieve method and field IDs */
static jboolean
gst_native_class_init(JNIEnv *env, jclass klass) {
    custom_data_field_id =
            (*env)->GetFieldID(env, klass, "nativeCustomData", "J");
    on_gstreamer_initialized_method_id =
            (*env)->GetMethodID(env, klass, "onGStreamerInitialized", "()V");
    on_ice_candidate_method_id =
            (*env)->GetMethodID(env, klass, "onIceCandidate",
                                "(IILjava/lang/String;)V");
    on_sdp_created_method_id =
            (*env)->GetMethodID(env, klass, "onSdpCreated",
                                "(ILjava/lang/String;Ljava/lang/String;)V");

    if (!custom_data_field_id
        || !on_gstreamer_initialized_method_id
        || !on_ice_candidate_method_id
        || !on_sdp_created_method_id) {
        /**
         * We emit this message through the Android log instead of the GStreamer log because the later
         * has not been initialized yet.
         */
        __android_log_print(ANDROID_LOG_ERROR, "camera-stream-pipeline",
                            "The calling class does not implement all necessary interface methods");
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

/** Webrtc feedback stuff */
static jlong
gst_native_start_preview(JNIEnv *env, jobject thiz, jint id,
                         jobjectArray j_stuns_array, jobjectArray j_turns_array) {

    gboolean ret;
    gint size = 0;
    GArray *turn_servers_array = NULL;
    GArray *stun_servers_array = NULL;

    CustomData *data = GET_CUSTOM_DATA(env, thiz, custom_data_field_id);
    if (!data)
        return 0;

    if (j_stuns_array) {
        size = (*env)->GetArrayLength(env, j_stuns_array);
        stun_servers_array = g_array_new(FALSE, FALSE, sizeof(gchar *));
        for (int i = 0; i < size; ++i) {
            jstring string = (*env)->GetObjectArrayElement(env, j_stuns_array, i);
            const char *stun_server = (*env)->GetStringUTFChars(env, string, 0);
            g_array_append_val(stun_servers_array, stun_server);
            (*env)->DeleteLocalRef(env, string);
        }
    }

    if (j_turns_array) {
        size = (*env)->GetArrayLength(env, j_turns_array);
        turn_servers_array = g_array_new(FALSE, FALSE, sizeof(gchar *));
        for (int i = 0; i < size; ++i) {
            jstring string = (*env)->GetObjectArrayElement(env, j_turns_array, i);
            const char *turn_server = (*env)->GetStringUTFChars(env, string, 0);
            g_array_append_val(turn_servers_array, turn_server);
            (*env)->DeleteLocalRef(env, string);
        }
    }

    g_signal_emit(data->webrtc_feedback_bin, gst_webrtc_feedback_bin_signals[START_PREVIEW_SIGNAL],
                  0,
                  id, turn_servers_array, stun_servers_array, &ret);
    return ret;
}

static void
gst_native_stop_preview(JNIEnv *env, jobject thiz, jint id) {
    gboolean ret;
    CustomData *data = GET_CUSTOM_DATA(env, thiz, custom_data_field_id);
    if (!data)
        return;
    g_signal_emit(data->webrtc_feedback_bin,
                  gst_webrtc_feedback_bin_signals[STOP_PREVIEW_SIGNAL], 0, id, &ret);
}

static void
gst_native_set_remote_description(JNIEnv *env, jobject thiz, jint id, jstring type, jstring sdp) {
    gboolean ret;
    CustomData *data = GET_CUSTOM_DATA(env, thiz, custom_data_field_id);
    if (!data)
        return;
    const gchar *type_str = (*env)->GetStringUTFChars(env, type, 0);
    const gchar *sdp_str = (*env)->GetStringUTFChars(env, sdp, 0);
    g_signal_emit(data->webrtc_feedback_bin,
                  gst_webrtc_feedback_bin_signals[SET_REMOTE_DESCRIPTION_SIGNAL],
                  0, id, type_str, sdp_str, &ret);

}

static void
gst_native_add_ice_candidate(JNIEnv *env, jobject thiz, jint id, jint m_line_index,
                             jstring candidate) {
    gboolean ret;
    CustomData *data = GET_CUSTOM_DATA(env, thiz, custom_data_field_id);
    if (!data)
        return;
    const gchar *candidate_str = (*env)->GetStringUTFChars(env, candidate, 0);
    g_signal_emit(data->webrtc_feedback_bin,
                  gst_webrtc_feedback_bin_signals[ADD_ICE_CANDIDATE_SIGNAL],
                  0, id, m_line_index, candidate_str, &ret);
}

/** Live publisher stuff */
static void
gst_native_start_stream(JNIEnv *env, jobject thiz, jint id, jobject live_profile_object) {
    gboolean ret;
    const char *url_str = "";
    const char *username_str = "";
    const char *password_str = "";
    const char *key_str = "";
    CustomData *data = GET_CUSTOM_DATA(env, thiz, custom_data_field_id);
    if (!data)
        return;

    /** Sanity checks */
    if (!live_profile_object) {
        jclass je = (*env)->FindClass(env, "java/lang/Exception");
        (*env)->ThrowNew(env, je, "Failed to start stream. Null input live profile parameter.");
        return;
    }

    /** Retrieve #LiveProfile methods ids */
    jclass live_profile_class = (*env)->GetObjectClass(env, live_profile_object);
    jmethodID get_url = (*env)->GetMethodID(env, live_profile_class, "getUrl",
                                            "()Ljava/lang/String;");
    jmethodID get_username = (*env)->GetMethodID(env, live_profile_class, "getUsername",
                                                 "()Ljava/lang/String;");
    jmethodID get_password = (*env)->GetMethodID(env, live_profile_class, "getPassword",
                                                 "()Ljava/lang/String;");
    jmethodID get_key = (*env)->GetMethodID(env, live_profile_class, "getKey",
                                            "()Ljava/lang/String;");

    if (!get_url || !get_username || !get_password || !get_key) {
        jclass je = (*env)->FindClass(env, "java/lang/Exception");
        (*env)->ThrowNew(env, je,
                         "Failed to start stream. Failed to get all #LiveProfile class methods ids.");
        return;
    }

    jstring j_url = (jstring) (*env)->CallObjectMethod(env, live_profile_object, get_url);
    jstring j_username = (jstring) (*env)->CallObjectMethod(env, live_profile_object, get_username);
    jstring j_password = (jstring) (*env)->CallObjectMethod(env, live_profile_object, get_password);
    jstring j_key = (jstring) (*env)->CallObjectMethod(env, live_profile_object, get_key);

    /** Convert the Java String to use it in C */
    if (j_url) {
        url_str = (*env)->GetStringUTFChars(env, j_url, 0);
    }
    if (j_username) {
        username_str = (*env)->GetStringUTFChars(env, j_username, 0);
    }
    if (j_password) {
        password_str = (*env)->GetStringUTFChars(env, j_password, 0);
    }
    if (j_key) {
        key_str = (*env)->GetStringUTFChars(env, j_key, 0);
    }

    GST_DEBUG("Create a live profile (host, username, password, key) "
              "VALUES (\"%s\", \"%s\", \"%s\",\"%s\")",
              url_str, username_str, password_str, key_str);

    char *full_url = g_strdup_printf("%s/%s", url_str, key_str);

    g_signal_emit(data->live_publisher_bin, gst_live_publisher_bin_signals[START_STREAM_SIGNAL],
                  0,
                  id, full_url, username_str, password_str, &ret);

    /** Release allocated resources */
    if (j_url) {
        (*env)->ReleaseStringUTFChars(env, j_url, url_str);
    }
    if (j_username) {
        (*env)->ReleaseStringUTFChars(env, j_username, username_str);
    }
    if (j_password) {
        (*env)->ReleaseStringUTFChars(env, j_password, password_str);
    }
    if (j_key) {
        (*env)->ReleaseStringUTFChars(env, j_key, key_str);
    }
    if (live_profile_class) {
        (*env)->DeleteLocalRef(env, live_profile_class);
    }
    g_free(full_url);
}

static void
gst_native_stop_stream(JNIEnv *env, jobject thiz, jint id) {
    gboolean ret;
    CustomData *data = GET_CUSTOM_DATA(env, thiz, custom_data_field_id);
    if (!data)
        return;
    g_signal_emit(data->live_publisher_bin, gst_live_publisher_bin_signals[STOP_STREAM_SIGNAL],
                  0,
                  id, &ret);
}


/** List of implemented native methods */
static JNINativeMethod native_methods[] = {
        {"nativeInit",                 "(Ljava/lang/String;)V",                                            (void *) gst_native_init},
        {"nativePlay",                 "()V",                                                              (void *) gst_native_play},
        {"nativeFinalize",             "()V",                                                              (void *) gst_native_finalize},
        {"nativePause",                "()V",                                                              (void *) gst_native_pause},
        {"nativeClassInit",            "()Z",                                                              (void *) gst_native_class_init},

        {"nativeStartStream",          "(ILcom/kalyzee/kontroller_services_api/dtos/video/LiveProfile;)V", (void *) gst_native_start_stream},
        {"nativeStopStream",           "(I)V",                                                             (void *) gst_native_stop_stream},

        {"nativeStartPreview",         "(I[Ljava/lang/String;[Ljava/lang/String;)J",                       (void *) gst_native_start_preview},
        {"nativeStopPreview",          "(I)V",                                                             (void *) gst_native_stop_preview},
        {"nativeSetRemoteDescription", "(ILjava/lang/String;Ljava/lang/String;)V",                         (void *) gst_native_set_remote_description},
        {"nativeAddIceCandidate",      "(IILjava/lang/String;)V",                                          (void *) gst_native_add_ice_candidate}
};

/** Library initializer */
jint
JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;

    java_vm = vm;

    if ((*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        __android_log_print(ANDROID_LOG_ERROR, "camera-stream-pipeline",
                            "Could not retrieve JNIEnv");
        return 0;
    }
    jclass klass = (*env)->FindClass(env,
                                     "org/freedesktop/gstreamer/pipeline/CameraStreamPipeline");
    (*env)->RegisterNatives(env, klass, native_methods,
                            G_N_ELEMENTS (native_methods));

    pthread_key_create(&current_jni_env, detach_current_thread);

    return JNI_VERSION_1_4;
}
