/**
* CONFIDENTIAL and PROPRIETARY software of KALYZEE SAS.
* Copyright (c) 2022 Kalyzee
* All rights reserved
* This copyright notice MUST be reproduced on all authorized copies.
*
* Authors : - Wassim BEN FATMA   <wassim.benfatma@kalyzee.com>
*/
#ifdef HAVE_CONFIG_H
#  include <config.h>
#endif

#include <gst/gst.h>
#include <gst/webrtc/webrtc.h>
#include <gst/sdp/sdp.h>
#include <stdbool.h>
#include <unistd.h>

#include "gstwebrtcfeedbackbin.h"

#define DEFAULT_MAX_SESSIONS 10


GST_DEBUG_CATEGORY_STATIC (gst_webrtc_feedback_bin_debug);
#define GST_CAT_DEFAULT gst_webrtc_feedback_bin_debug

guint gst_webrtc_feedback_bin_signals[LAST_SIGNAL] = {0};

/**
 * the capabilities of the inputs and outputs.
 * describe the real formats here.
 */
static GstStaticPadTemplate sink_audio_factory =
        GST_STATIC_PAD_TEMPLATE(
                "audio_sink",
                GST_PAD_SINK,
                GST_PAD_ALWAYS,
                GST_STATIC_CAPS("application/x-rtp"));

static GstStaticPadTemplate sink_video_factory =
        GST_STATIC_PAD_TEMPLATE(
                "video_sink",
                GST_PAD_SINK,
                GST_PAD_ALWAYS,
                GST_STATIC_CAPS("application/x-rtp"));

static const gchar webrtcbin_video_caps_str[] =
        "application/x-rtp,media=video,encoding-name=H264,payload=96";

static const gchar webrtcbin_audio_caps_str[] =
        "application/x-rtp,media=audio,"
        "encoding-name=OPUS, payload=96, encoding-params=(string)2, clock-rate=48000, channels=(int)2";


#define gst_webrtc_feedback_bin_parent_class parent_class

G_DEFINE_TYPE (Gstwebrtcfeedbackbin, gst_webrtc_feedback_bin, GST_TYPE_BIN);

GST_ELEMENT_REGISTER_DEFINE (webrtcfeedbackbin, "webrtcfeedbackbin", GST_RANK_NONE,
                             GST_TYPE_WEBRTCFEEDBACKBIN);

static void gst_webrtc_feedback_bin_set_property(GObject *object,
                                                 guint prop_id, const GValue *value,
                                                 GParamSpec *pspec);

static void destroy_receiver_entry(gpointer receiver_entry_ptr) {

    /** Sanity checks */
    if (!receiver_entry_ptr) {
        g_print("%s: Kast WEBRTC - NULL input pointer.", __FUNCTION__);
        return;
    }

    ReceiverEntry *receiver_entry = (ReceiverEntry *) receiver_entry_ptr;

    if (receiver_entry->webrtcbin) {
        gst_element_set_state(GST_ELEMENT (receiver_entry->webrtcbin),
                              GST_STATE_NULL);
        gst_object_unref(GST_OBJECT(receiver_entry->webrtcbin));
        receiver_entry->webrtcbin = NULL;
        g_print("%s: Kast WEBRTC - webrtcbin freed", __FUNCTION__);
    }

    g_mutex_clear(&receiver_entry->mutex);
    g_slice_free1(sizeof(ReceiverEntry), receiver_entry);
}

static void on_offer_created_cb(GstPromise *promise, gpointer user_data) {
    guint i;
    gchar *sdp_string;
    GstStructure const *reply;
    GstSDPMedia *video_media = NULL;
    GstPromise *local_desc_promise;
    GstWebRTCSessionDescription *offer = NULL;
    ReceiverEntry *receiver_entry = (ReceiverEntry *) user_data;

    /** Create SDP offer */
    reply = gst_promise_get_reply(promise);
    gst_structure_get(reply, "offer", GST_TYPE_WEBRTC_SESSION_DESCRIPTION, &offer, NULL);
    gst_promise_unref(promise);
    /**
     * Noted that sometimes created SDP misses H264 profile-level-id which causes SDP negotiation failure with some browsers.
     * Moreover, some browsers don't accept SDPs for H264 that contains profile-level-id different from 42e01f
     * To avoid these issues, we are constrained to set manually the H256 profile level id attribute in the SDP video media
     */
    video_media = (GstSDPMedia *) &g_array_index(offer->sdp->medias, GstSDPMedia, 0);
    if (!video_media) {
        GST_WARNING_OBJECT(gst_object_get_parent(receiver_entry->webrtcbin), "Empty video media.");
        return;
    }

    for (i = 0; i < video_media->attributes->len; i++) {
        const GstSDPAttribute *attr = gst_sdp_media_get_attribute(video_media, i);
        if (g_strcmp0(attr->key, "fmtp") == 0) {
            gst_sdp_media_remove_attribute(video_media, i);
        }
    }

    gst_sdp_media_add_attribute(video_media, "fmtp",
                                "96 packetization-mode=1;profile-level-id=42e01f;level-asymmetry-allowed=1");

    local_desc_promise = gst_promise_new();
    g_signal_emit_by_name(receiver_entry->webrtcbin, "set-local-description", offer,
                          local_desc_promise);
    gst_promise_interrupt(local_desc_promise);
    gst_promise_unref(local_desc_promise);
    sdp_string = gst_sdp_message_as_text(offer->sdp);

    GST_INFO_OBJECT(gst_object_get_parent(receiver_entry->webrtcbin),
                    " Negotiation offer created:\n%s\n",
                    sdp_string);
    /** Send the created SDP offer t the remote peer */
    g_signal_emit(gst_element_get_parent(receiver_entry->webrtcbin),
                  gst_webrtc_feedback_bin_signals[ON_SDP_CREATED_SIGNAL],
                  0, receiver_entry->id, "offer", sdp_string);

    /** Free resources */
    g_free(sdp_string);
    gst_webrtc_session_description_free(offer);
}

static void on_negotiation_needed_cb(GstElement *webrtcbin, gpointer user_data) {
    GstPromise *promise;
    ReceiverEntry *receiver_entry = (ReceiverEntry *) user_data;

    g_mutex_lock(&receiver_entry->mutex);
    promise = gst_promise_new_with_change_func(on_offer_created_cb, (gpointer) receiver_entry,
                                               NULL);
    g_signal_emit_by_name(G_OBJECT (webrtcbin), "create-offer", NULL, promise);
    g_mutex_unlock(&receiver_entry->mutex);

}

static void on_ice_candidate_cb(G_GNUC_UNUSED GstElement *webrtcbin, guint m_line_index,
                                gchar *candidate, gpointer user_data) {
    ReceiverEntry *receiver_entry = (ReceiverEntry *) user_data;

    g_mutex_lock(&receiver_entry->mutex);

    GST_INFO_OBJECT(gst_object_get_parent(webrtcbin),
                    "Ice candidate created: m_line_index: %d, candidate:%s ",
                    m_line_index, candidate);

    /** Send the ICE candidate to the remote peer */
    g_signal_emit(gst_element_get_parent(receiver_entry->webrtcbin),
                  gst_webrtc_feedback_bin_signals[ON_ICE_CANDIDATE_SIGNAL],
                  0, receiver_entry->id, m_line_index, candidate);

    g_mutex_unlock(&receiver_entry->mutex);
}

static gboolean
gst_webrtc_feedback_bin_set_remote_description(Gstwebrtcfeedbackbin *self, gint id,
                                               const gchar *type_str,
                                               const gchar *sdp_str) {
    GstPromise *promise;
    GstSDPMessage *sdp;
    GstWebRTCSessionDescription *answer;
    gint ret;
    ReceiverEntry *receiver_entry;

    /** Sanity checks */
    if (!self || !sdp_str || !type_str) {
        GST_WARNING_OBJECT(self, "Null input parameters!");
        return FALSE;
    }

    if (g_hash_table_lookup_extended(self->receiver_entry_table, GINT_TO_POINTER(id), NULL,
                                     (gpointer *) &receiver_entry)) {
        if (g_strcmp0(type_str, "answer") != 0) {
            GST_WARNING_OBJECT(self,
                               "Expected SDP message type \"answer\", got \"%s\"\n",
                               type_str);
            return FALSE;
        }
        GST_INFO_OBJECT(self, "Received SDP:\n%s\n len %zd",
                        sdp_str,
                        strlen(sdp_str));
        ret = gst_sdp_message_new(&sdp);
        if (ret != GST_SDP_OK) {
            GST_WARNING_OBJECT(self, "gst_sdp_message_new failed.\n");
            return FALSE;
        }
        ret = gst_sdp_message_parse_buffer((guint8 *) sdp_str, strlen(sdp_str), sdp);
        if (ret != GST_SDP_OK) {
            GST_WARNING_OBJECT(self, "Could not parse SDP string.\n");
            return FALSE;
        }
        answer = gst_webrtc_session_description_new(GST_WEBRTC_SDP_TYPE_ANSWER, sdp);
        if (!answer) {
            GST_WARNING_OBJECT(self, "gst_webrtc_session_description_new returned NULL answer\n");
            return FALSE;
        }

        promise = gst_promise_new();
        g_signal_emit_by_name(receiver_entry->webrtcbin, "set-remote-description", answer, promise);
        gst_promise_interrupt(promise);
        gst_promise_unref(promise);
        gst_webrtc_session_description_free(answer);

    }
    return TRUE;
}

static gboolean
gst_webrtc_feedback_bin_add_ice_candidate(Gstwebrtcfeedbackbin *self, gint id,
                                          guint m_line_index, gchar *candidate) {

    ReceiverEntry *receiver_entry;
    /** Sanity checks */
    if (!self || !candidate) {
        GST_WARNING_OBJECT(self, "Null input parameters!");
        return FALSE;
    }

    if (g_hash_table_lookup_extended(self->receiver_entry_table, GINT_TO_POINTER(id), NULL,
                                     (gpointer *) &receiver_entry)) {
        GST_INFO_OBJECT(self, "Received ICE candidate with mline index %u; candidate: %s\n",
                        m_line_index, candidate);
        g_signal_emit_by_name(receiver_entry->webrtcbin, "add-ice-candidate",
                              m_line_index, candidate);
    }
    return TRUE;
}

static gboolean
gst_webrtc_feedback_bin_start_preview(Gstwebrtcfeedbackbin *self,
                                      gint id,
                                      GArray *turns_array,
                                      GArray *stuns_array) {
    gint ret;
    gchar *audio_queue_name, *video_queue_name;
    gchar *peer_id;
    GstElement *webrtcbin, *webrtc_audio_queue, *webrtc_video_queue;
    guint i = 0;
    GstWebRTCRTPTransceiver *transceiver;
    GArray *transceivers;

    /** Sanity checks */
    if (!self || !turns_array || !stuns_array) {
        GST_WARNING_OBJECT(self, "Null input parameters!");
        return FALSE;
    }

    /**
     * Check if #max_webrtc_sessions is reached. #max_webrtc_sessions specifies the maximum number of
     * webrtc connections that can be active simultaneously.
     */
    if (g_hash_table_size(self->receiver_entry_table) >= self->max_webrtc_sessions) {
        GST_WARNING_OBJECT(self,
                           "The maximum number of webrtc connections that can be active simultaneously (%d) is reached.",
                           self->max_webrtc_sessions);
        return FALSE;
    }
    if (g_hash_table_lookup(self->receiver_entry_table, GINT_TO_POINTER(id))) {
        GST_WARNING_OBJECT(self, "webrtcbin-%d already added. \n", id);
        return FALSE;
    }

    GST_INFO_OBJECT(self, "Start preview session requested. Add peer %d to webrtc feedback pipeline.", id);

    /**
     * Create webrtc audio queue, webrtc video queue and webrtcbin,
     * and add them to #webrtc_feedback_pipeline.
     */
    peer_id = g_strdup_printf("webrtcbin-%d", id);

    audio_queue_name = g_strdup_printf("webrtc-audio-queue-%s", peer_id);
    webrtc_audio_queue = gst_element_factory_make("queue", audio_queue_name);
    g_object_set(G_OBJECT (webrtc_audio_queue), "leaky", 2, NULL);
    g_free(audio_queue_name);

    video_queue_name = g_strdup_printf("webrtc-video-queue-%s", peer_id);
    webrtc_video_queue = gst_element_factory_make("queue", video_queue_name);
    g_object_set(G_OBJECT (webrtc_video_queue), "leaky", 2, NULL);
    g_free(video_queue_name);

    webrtcbin = gst_element_factory_make("webrtcbin", peer_id);
    g_object_set(G_OBJECT (webrtc_audio_queue), "leaky", 2, NULL);
    /** Set bundle policy to max-bundle */
    g_object_set(G_OBJECT (webrtcbin), "bundle-policy",
                 GST_WEBRTC_BUNDLE_POLICY_MAX_BUNDLE, NULL);
    /** Set turn and stun servers */
    if (turns_array) {
        for (i = 0; i < turns_array->len; i++) {
            char *turn_server = g_array_index(turns_array, gchar*, i);
            GST_INFO_OBJECT(self, "Webrtc-%d - turn server (%d) is set to webrtcbin: %s.", id, i,
                            turn_server);
            g_object_set(G_OBJECT (webrtcbin), "turn-server",
                         turn_server, NULL);
        }
    }
    /** Set default google stun server */
    g_object_set(G_OBJECT (webrtcbin), "stun-server",
                 "stun://stun.l.google.com:19302", NULL);
    if (stuns_array) {
        for (i = 0; i < stuns_array->len; i++) {
            char *stun_server = g_array_index(stuns_array, gchar*, i);
            GST_INFO_OBJECT(self, "Webrtc-%d - stun server (%d) is set to webrtcbin: %s.", id, i,
                            stun_server);
            g_object_set(G_OBJECT (webrtcbin), "stun-server", stun_server, NULL);
        }
    }

    ReceiverEntry *receiver_entry = g_slice_alloc0(sizeof(ReceiverEntry));
    receiver_entry->id = id;
    receiver_entry->webrtcbin = webrtcbin;
    g_mutex_init(&receiver_entry->mutex);
    /**
     * Using a mutex for each ReceiverEntry allowing to synchronize add_peer_to_pipeline, remove_peer_from_pipeline
     * and the webrtcbin signals callbacks.
     */
    g_mutex_lock(&receiver_entry->mutex);

    g_signal_connect (receiver_entry->webrtcbin, "on-negotiation-needed",
                      G_CALLBACK(on_negotiation_needed_cb), (gpointer) receiver_entry);

    g_signal_connect (receiver_entry->webrtcbin, "on-ice-candidate",
                      G_CALLBACK(on_ice_candidate_cb), (gpointer) receiver_entry);

    g_free(peer_id);
    gst_bin_add_many(self,
                     webrtc_video_queue, webrtcbin, NULL);
    GST_INFO_OBJECT(self,
                    "webrtc_audio_queue, webrtc_video_queue and webrtcbin elements are added with success to webrtc feedback pipeline.");

    /**
     * Link  webrtc_audio_tee --> webrtc_audio_queue --> webrtcbin
     * & webrtc_video_tee --> webrtc_video_queue --> webrtcbin
     */
    /*ret = gst_element_link(self->webrtc_audio_tee, webrtc_audio_queue);
    if (ret != TRUE) {
        GST_ERROR_OBJECT(self,
                         "%s: Kast WEBRTC - Failed to link webrtc_audio_tee to webrtc_audio_queue.",
                         __FUNCTION__);
        goto add_peer_to_pipeline_cleanup;
    }
    GstCaps *const webrtcbin_audio_caps = gst_caps_from_string(webrtcbin_audio_caps_str);
    ret = gst_element_link_filtered(
            webrtc_audio_queue,
            webrtcbin,
            webrtcbin_audio_caps);
    if (ret != TRUE) {
        GST_ERROR_OBJECT(self,
                           "Failed to link webrtc_audio_queue to webrtcbin.");
        goto add_peer_to_pipeline_cleanup;
    }

    gst_caps_unref(GST_CAPS(webrtcbin_audio_caps));

    GST_INFO_OBJECT(self,
                    "webrtc_audio_tee --> webrtc_audio_queue --> webrtcbin are linked with success.");
*/
    /**
     * Sync added elements with parent pipeline
     * PS:
     *  A normal flow consists of creating webrtcbin and all other related elements (tee port, queue ...), linking them together,
     *  registering webrtcbin callbacks and after that calling sync_state_with_parent.
     *  Trying this flow, the negotiation-needed event almost never fires.
     *  If we first create elements, then sync_state_with_parent and after that start to connect pads -
     *  --> This flow leads to reliable and working connection.  100% of the time the offer is created
     *  and the connection is established.
     */
    /*ret = gst_element_sync_state_with_parent(webrtc_audio_queue);
    if (!ret) {
        GST_WARNING_OBJECT(self, "Failed to sync webrtc_audio_queue with parent.");

        goto add_peer_to_pipeline_cleanup;
    }*/
    ret = gst_element_sync_state_with_parent(webrtc_video_queue);
    if (!ret) {
        GST_WARNING_OBJECT(self, "Failed to sync webrtc_video_queue with parent.");
        goto gst_webrtc_feedback_bin_start_preview_cleanup;
    }
    ret = gst_element_sync_state_with_parent(webrtcbin);
    if (!ret) {
        GST_WARNING_OBJECT(self, "Failed to sync webrtcbin with parent.");
        goto gst_webrtc_feedback_bin_start_preview_cleanup;
    }
    ret = gst_element_link_many(self->webrtc_video_tee, webrtc_video_queue, webrtcbin, NULL);
    if (ret != TRUE) {
        GST_WARNING_OBJECT(self,
                           "Failed to link webrtc_video_tee -> webrtc_video_queue -> webrtcbin.");
        goto gst_webrtc_feedback_bin_start_preview_cleanup;
    }
    sleep(5);
    g_signal_emit_by_name(webrtcbin, "get-transceivers", &transceivers);
    if ((!transceivers) || transceivers->len <= 0) {
        GST_WARNING_OBJECT(gst_object_get_parent(webrtcbin), "Failed to get webrtcbin transceivers.");
        return FALSE;
    }
    transceiver = g_array_index (transceivers, GstWebRTCRTPTransceiver *, 0);
    g_object_set(transceiver, "direction", GST_WEBRTC_RTP_TRANSCEIVER_DIRECTION_SENDONLY, NULL);
    transceiver = g_array_index (transceivers, GstWebRTCRTPTransceiver *, 1);
    g_object_set(transceiver, "direction", GST_WEBRTC_RTP_TRANSCEIVER_DIRECTION_SENDONLY, NULL);
    g_array_unref(transceivers);

    GST_INFO_OBJECT(self,
                    "webrtc_video_tee --> webrtc_video_queue --> webrtcbin are linked with success.");

    /** Insert the new receiver_entry to receiver_entry_table */
    g_hash_table_replace(self->receiver_entry_table, GINT_TO_POINTER(id), receiver_entry);
    GST_INFO_OBJECT(self,
                    "New webrtc client is connected. id: %d, number of webrtc clients currently connected = %d \n",
                    id, g_hash_table_size(self->receiver_entry_table));

gst_webrtc_feedback_bin_start_preview_cleanup:
    /** Cleanup */
    if (turns_array) {
        g_array_free(turns_array, TRUE);
        turns_array = NULL;
    }
    if (stuns_array) {
        g_array_free(stuns_array, TRUE);
        stuns_array = NULL;
    }
    g_mutex_unlock(&receiver_entry->mutex);

    return TRUE;
}

static gboolean
gst_webrtc_feedback_bin_stop_preview(Gstwebrtcfeedbackbin *self,
                                     glong id) {
    gchar *audio_queue_name, *video_queue_name;
    gchar *peer_id;
    ReceiverEntry *receiver_entry;
    GstElement *webrtcbin, *webrtc_audio_queue, *webrtc_video_queue;
    GstPad *webrtc_audio_sink_pad, *webrtc_video_sink_pad,
            *webrtc_audio_tee_src_pad, *webrtc_video_tee_src_pad;

    /** Sanity checks */
    if (!self) {
        GST_WARNING_OBJECT(self, "Null input parameters!");
        return FALSE;
    }
    if (!(self->receiver_entry_table)) {
        GST_WARNING_OBJECT(self, "Null receiver_entry_table pointer \n");
        return FALSE;
    }
    if (!g_hash_table_lookup_extended(self->receiver_entry_table, GINT_TO_POINTER(id), NULL,
                                      (gpointer *) &receiver_entry)) {
        GST_WARNING_OBJECT(self, "Peer connection %d has not been opened.", id);
        return FALSE;

    }
    GST_INFO_OBJECT(self, "Remove peer %d from webrtc feedback pipeline.", id);

    g_mutex_lock(&receiver_entry->mutex);
    peer_id = g_strdup_printf("webrtcbin-%d", id);

    webrtcbin = gst_bin_get_by_name(GST_BIN (self), peer_id);
    if (!webrtcbin) {
        GST_WARNING_OBJECT(self,
                           "webrtcbin-%d is not found in webrtc feedback pipeline.", id);
        goto gst_webrtc_feedback_bin_stop_preview_cleanup;
    }
    gst_bin_remove(GST_BIN (self), webrtcbin);

    /*
    audio_queue_name = g_strdup_printf("webrtc-audio-queue-%s", peer_id);
    webrtc_audio_queue = gst_bin_get_by_name(GST_BIN (self),
                                             audio_queue_name);
    if (!webrtc_audio_queue) {
        GST_WARNING_OBJECT(self, "Failed to get webrtc_audio_queue.");
        g_free(audio_queue_name);
        g_free(peer_id);
        goto gst_webrtc_feedback_bin_stop_preview_cleanup;
    }
    g_free(audio_queue_name); */


    /** Remove webrtc_audio_queue */
    /*webrtc_audio_sink_pad = gst_element_get_static_pad(webrtc_audio_queue, "sink");
    if (!webrtc_audio_sink_pad) {
        GST_WARNING_OBJECT(self, "Failed to get webrtc_audio_sink_pad.");
        goto gst_webrtc_feedback_bin_stop_preview_cleanup;
    }

    webrtc_audio_tee_src_pad = gst_pad_get_peer(webrtc_audio_sink_pad);
    if (!webrtc_audio_tee_src_pad) {
        GST_WARNING_OBJECT(self, "Failed to get webrtc_audio_tee_src_pad.");
        goto gst_webrtc_feedback_bin_stop_preview_cleanup;
    }
    gst_object_unref(GST_OBJECT(webrtc_audio_sink_pad));*/
    /**
     * NB: #gst_bin_remove removes the element from the bin, unparenting it as well.
     * Unparenting the element means that the element will be dereferenced,
     * so if the bin holds the only reference to the element,
     * the element will be freed in the process of removing it from the bin
     */
    /*gst_bin_remove(GST_BIN (self), webrtc_audio_queue);

    gst_element_release_request_pad(self->webrtc_audio_tee, webrtc_audio_tee_src_pad);
    gst_object_unref(GST_OBJECT(webrtc_audio_tee_src_pad)); */

    /** Remove video stuff */
    video_queue_name = g_strdup_printf("webrtc-video-queue-%s", peer_id);
    webrtc_video_queue = gst_bin_get_by_name(GST_BIN (self),
                                             video_queue_name);
    if (!webrtc_video_queue) {
        GST_WARNING_OBJECT(self, "Failed to get webrtc_video_queue.");
        g_free(video_queue_name);
        g_free(peer_id);
        goto gst_webrtc_feedback_bin_stop_preview_cleanup;
    }
    g_free(video_queue_name);
    g_free(peer_id);

    webrtc_video_sink_pad = gst_element_get_static_pad(webrtc_video_queue, "sink");
    if (!webrtc_video_sink_pad) {
        GST_WARNING_OBJECT(self, "Failed to get webrtc_video_sink_pad.");
        goto gst_webrtc_feedback_bin_stop_preview_cleanup;
    }

    webrtc_video_tee_src_pad = gst_pad_get_peer(webrtc_video_sink_pad);
    if (!webrtc_video_tee_src_pad) {
        GST_WARNING_OBJECT(self, "Failed to get webrtc_video_tee_src_pad.");
        goto gst_webrtc_feedback_bin_stop_preview_cleanup;
    }
    gst_object_unref(GST_OBJECT(webrtc_video_sink_pad));

    gst_bin_remove(GST_BIN (self), webrtc_video_queue);

    gst_element_release_request_pad(self->webrtc_video_tee, webrtc_video_tee_src_pad);
    gst_object_unref(GST_OBJECT(webrtc_video_tee_src_pad));

gst_webrtc_feedback_bin_stop_preview_cleanup:
    /** Remove corresponding webrtc peer from webrtc feedback pipeline */
    g_hash_table_remove(self->receiver_entry_table, GINT_TO_POINTER(id));
    GST_INFO_OBJECT(self,
                    "Removed webrtcbin: %d, number of webrtc clients currently connected = %d.",
                    id,
                    g_hash_table_size(self->receiver_entry_table));
    g_mutex_unlock(&receiver_entry->mutex);

    return TRUE;
}

static void gst_webrtc_feedback_bin_get_property(GObject *object,
                                                 guint prop_id, GValue *value, GParamSpec *pspec);

/** GObject vmethod implementations */

/** Initialize the webrtcfeedbackbin's class */
static void
gst_webrtc_feedback_bin_class_init(GstwebrtcfeedbackbinClass *klass) {
    GObjectClass *gobject_class = (GObjectClass *) klass;
    GstElementClass *gstelement_class = (GstElementClass *) klass;

    gobject_class = (GObjectClass *) klass;
    gstelement_class = (GstElementClass *) klass;

    gobject_class->set_property = gst_webrtc_feedback_bin_set_property;
    gobject_class->get_property = gst_webrtc_feedback_bin_get_property;

    g_object_class_install_property(gobject_class, PROP_MAX_SESSIONS,
                                    g_param_spec_uint("max-webrtc-sessions", "Max webrtc sessions",
                                                      "Max webrtc sessions", 0, G_MAXUINT32,
                                                      DEFAULT_MAX_SESSIONS,
                                                      G_PARAM_READWRITE));
    gst_webrtc_feedback_bin_signals[START_PREVIEW_SIGNAL] =
            g_signal_newv("start-preview", G_TYPE_FROM_CLASS(klass),
                          G_SIGNAL_RUN_LAST | G_SIGNAL_ACTION,
                          g_cclosure_new(G_CALLBACK(gst_webrtc_feedback_bin_start_preview),
                                         NULL, NULL),
                          NULL, NULL, NULL,
                          G_TYPE_BOOLEAN, // Return TRUE in case of success, FALSE otherwise
                          3, (GType[3]) {G_TYPE_INT, G_TYPE_ARRAY, G_TYPE_ARRAY});
    gst_webrtc_feedback_bin_signals[STOP_PREVIEW_SIGNAL] =
            g_signal_newv("stop-preview", G_TYPE_FROM_CLASS(klass),
                          G_SIGNAL_RUN_LAST | G_SIGNAL_ACTION,
                          g_cclosure_new(G_CALLBACK(gst_webrtc_feedback_bin_stop_preview),
                                         NULL, NULL),
                          NULL, NULL, NULL,
                          G_TYPE_BOOLEAN, // Return TRUE in case of success, FALSE otherwise
                          1, (GType[1]) {G_TYPE_INT});
    gst_webrtc_feedback_bin_signals[SET_REMOTE_DESCRIPTION_SIGNAL] =
            g_signal_newv("set-remote-description", G_TYPE_FROM_CLASS(klass),
                          G_SIGNAL_RUN_LAST | G_SIGNAL_ACTION,
                          g_cclosure_new(G_CALLBACK(gst_webrtc_feedback_bin_set_remote_description),
                                         NULL, NULL),
                          NULL, NULL, NULL,
                          G_TYPE_BOOLEAN, // Return TRUE in case of success, FALSE otherwise
                          3, (GType[3]) {G_TYPE_INT, G_TYPE_STRING, G_TYPE_STRING});
    gst_webrtc_feedback_bin_signals[ADD_ICE_CANDIDATE_SIGNAL] =
            g_signal_newv("add-ice-candidate", G_TYPE_FROM_CLASS(klass),
                          G_SIGNAL_RUN_LAST | G_SIGNAL_ACTION,
                          g_cclosure_new(G_CALLBACK(gst_webrtc_feedback_bin_add_ice_candidate),
                                         NULL, NULL),
                          NULL, NULL, NULL,
                          G_TYPE_BOOLEAN, // Return TRUE in case of success, FALSE otherwise
                          3, (GType[3]) {G_TYPE_INT, G_TYPE_UINT, G_TYPE_STRING});
    gst_webrtc_feedback_bin_signals[ON_SDP_CREATED_SIGNAL] =
            g_signal_new("on-sdp-created", G_TYPE_FROM_CLASS (klass),
                         G_SIGNAL_RUN_LAST, 0, NULL, NULL, NULL,
                         G_TYPE_NONE, 3, G_TYPE_INT, G_TYPE_STRING, G_TYPE_STRING);
    gst_webrtc_feedback_bin_signals[ON_ICE_CANDIDATE_SIGNAL] =
            g_signal_new("on-ice-candidate", G_TYPE_FROM_CLASS (klass),
                         G_SIGNAL_RUN_LAST, 0, NULL, NULL, NULL,
                         G_TYPE_NONE, 3, G_TYPE_INT, G_TYPE_UINT, G_TYPE_STRING);

    gst_element_class_set_details_simple(gstelement_class,
                                         "Webrtc feedback bin",
                                         "Webrtc feedback bin",
                                         "Webrtc feedback bin",
                                         "Wassim BEN FATMA <<wassim.benfatma@kalyzee.com>>");

    gst_element_class_add_pad_template(gstelement_class,
                                       gst_static_pad_template_get(&sink_video_factory));
    gst_element_class_add_pad_template(gstelement_class,
                                       gst_static_pad_template_get(&sink_audio_factory));
}

/**
 * Initialize the new element
 * Instantiate pads and add them to element
 * Set pad callback functions
 * Initialize instance structure
 */
static void
gst_webrtc_feedback_bin_init(Gstwebrtcfeedbackbin *self) {

    gboolean ret = FALSE;

    self->max_webrtc_sessions = DEFAULT_MAX_SESSIONS;
    self->receiver_entry_table = g_hash_table_new_full(g_direct_hash, g_direct_equal, NULL,
                                                       destroy_receiver_entry);
    self->webrtc_video_tee = gst_element_factory_make("tee", "webrtc_video_tee");
    if (!self->webrtc_video_tee) {
        GST_ERROR_OBJECT(self, "Failed to create webrtc_video_tee element.");
        return;
    }
    g_object_set(G_OBJECT ( self->webrtc_video_tee), "allow-not-linked", TRUE, NULL);


    self->webrtc_audio_tee = gst_element_factory_make("tee", "webrtc_audio_tee");
    if (!self->webrtc_audio_tee) {
        GST_ERROR_OBJECT(self, "Failed to create webrtc_audio_tee element.");
        return;
    }
    g_object_set(G_OBJECT ( self->webrtc_audio_tee), "allow-not-linked", TRUE, NULL);

    /** Link all elements that can be automatically linked because they have "Always" pads */
    gst_bin_add_many(GST_BIN (self), self->webrtc_video_tee,/*
                     /*self->webrtc_audio_tee , */
                     NULL);

    GstPad *sink_video_pad = gst_element_get_static_pad(self->webrtc_video_tee, "sink");
    ret = gst_element_add_pad(GST_ELEMENT(self),
                              gst_ghost_pad_new_from_template("sink", sink_video_pad,
                                                              gst_static_pad_template_get(
                                                                      &sink_video_factory)));
    gst_object_unref(GST_OBJECT(sink_video_pad));
    if (!ret) {
        GST_ERROR_OBJECT(self, "Failed to add video ghost pad to the bin");
        return;
    }

    GST_DEBUG_OBJECT(self, "Added video sink pad to the bin");
/*
    GstPad *sink_audio_pad = gst_element_get_static_pad(self->webrtc_audio_tee, "sink");
    ret = gst_element_add_pad(GST_ELEMENT(self),
                              gst_ghost_pad_new_from_template("audio_sink", sink_audio_pad,
                                                              gst_static_pad_template_get(
                                                                      &sink_audio_factory)));
    gst_object_unref(GST_OBJECT(sink_audio_pad));
    if (!ret) {
        GST_ERROR_OBJECT(self, "Failed to add audio ghost pad to the bin.");
        return;
    }

    GST_DEBUG_OBJECT(self, "Added audio sink pad to the bin with success.");
*/
}

static void
gst_webrtc_feedback_bin_set_property(GObject *object, guint prop_id,
                                     const GValue *value, GParamSpec *pspec) {
    Gstwebrtcfeedbackbin *self = GST_WEBRTC_FEEDBACK_BIN(object);

    switch (prop_id) {
        case PROP_MAX_SESSIONS:
            self->max_webrtc_sessions = g_value_get_uint(value);
            break;
        default:
            G_OBJECT_WARN_INVALID_PROPERTY_ID (object, prop_id, pspec);
            break;
    }
}

static void
gst_webrtc_feedback_bin_get_property(GObject *object, guint prop_id,
                                     GValue *value, GParamSpec *pspec) {
    Gstwebrtcfeedbackbin *self = GST_WEBRTC_FEEDBACK_BIN(object);

    switch (prop_id) {
        case PROP_MAX_SESSIONS:
            g_value_set_uint(value, self->max_webrtc_sessions);
            break;
        default:
            G_OBJECT_WARN_INVALID_PROPERTY_ID (object, prop_id, pspec);
            break;
    }
}

/* GstElement vmethod implementations */

/**
 * Entry point to initialize the plug-in
 * initialize the plug-in itself
 * register the element factories and other features
 */
gboolean
webrtc_feedback_bin_init(GstPlugin *webrtcfeedbackbin) {
    GST_DEBUG_CATEGORY_INIT (gst_webrtc_feedback_bin_debug, "webrtcfeedbackbin",
                             0, "A Gstreamer bin allowing to retrieve webrtc feedback");

    return GST_ELEMENT_REGISTER (webrtcfeedbackbin, webrtcfeedbackbin);
}

#ifndef PACKAGE
#define PACKAGE "webrtcfeedbackbin"
#endif

GST_PLUGIN_DEFINE (GST_VERSION_MAJOR,
                   GST_VERSION_MINOR,
                   webrtcfeedbackbin,
                   "webrtcfeedbackbin",
                   webrtc_feedback_bin_init,
                   "", "LGPL", "GStreamer", "http://gstreamer.net/")
