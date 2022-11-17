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

#include "gstlivepublisherbin.h"

#define DEFAULT_MAX_SESSIONS 10

GST_DEBUG_CATEGORY_STATIC (gst_live_publisher_bin_debug);
#define GST_CAT_DEFAULT gst_live_publisher_bin_debug

guint gst_live_publisher_bin_signals[LAST_LIVE_PUBLISHER_SIGNAL] = {0};

/**
 * the capabilities of the inputs and outputs.
 * describe the real formats here.
 */
static GstStaticPadTemplate sink_audio_factory =
        GST_STATIC_PAD_TEMPLATE(
                "audio_sink",
                GST_PAD_SINK,
                GST_PAD_ALWAYS,
                GST_STATIC_CAPS("audio/x-raw"));

static GstStaticPadTemplate sink_video_factory =
        GST_STATIC_PAD_TEMPLATE(
                "video_sink",
                GST_PAD_SINK,
                GST_PAD_ALWAYS,
                GST_STATIC_CAPS("video/x-h264"));

#define gst_live_publisher_bin_parent_class parent_class

G_DEFINE_TYPE (Gstlivepublisherbin, gst_live_publisher_bin, GST_TYPE_BIN);

GST_ELEMENT_REGISTER_DEFINE (livepublisherbin, "livepublisherbin", GST_RANK_NONE,
                             GST_TYPE_LIVEPUBLISHERBIN);

static void gst_live_publisher_bin_set_property(GObject *object,
                                                guint prop_id, const GValue *value,
                                                GParamSpec *pspec);

static void gst_live_publisher_bin_get_property(GObject *object,
                                                guint prop_id, GValue *value, GParamSpec *pspec);

static gboolean
gst_live_publisher_bin_start_stream(Gstlivepublisherbin *self,
                                    gint id,
                                    const gchar *location,
                                    const gchar *username,
                                    const gchar *password) {
    gchar *video_queue_name, *flvmux_name, *rtmpsink_name;
    gchar *peer_id;
    GstElement *rtmpsink, *flvmux, *video_queue;
    gboolean ret = TRUE;

    peer_id = g_strdup_printf("%d", id);

    /** Create video_queue, flvmux and rtmpssink elements. */
    video_queue_name = g_strdup_printf("live-video-queue-%s", peer_id);
    video_queue = gst_element_factory_make("queue", video_queue_name);
    g_object_set(G_OBJECT (video_queue), "leaky", 2, NULL);
    g_free(video_queue_name);

    flvmux_name = g_strdup_printf("flvmux-%s", peer_id);
    flvmux = gst_element_factory_make("flvmux", flvmux_name);
    g_free(flvmux_name);
    g_object_set(G_OBJECT (flvmux), "streamable", TRUE, NULL);

    rtmpsink_name = g_strdup_printf("rtmpsink-%s", peer_id);
    rtmpsink = gst_element_factory_make("rtmp2sink", rtmpsink_name);
    g_object_set(G_OBJECT(rtmpsink), "location", location, NULL);
    g_object_set(G_OBJECT(rtmpsink), "username", username, NULL);
    g_object_set(G_OBJECT(rtmpsink), "password", password, NULL);
    g_object_set(G_OBJECT(rtmpsink), "sync", FALSE, NULL);
    g_free(rtmpsink_name);

    char *audio_test_src_name = g_strdup_printf("audio-test-src-%s", peer_id);
    char *avenc_aac_name = g_strdup_printf("avenc-aac-%s", peer_id);
    GstElement *audio_test = gst_element_factory_make("audiotestsrc", audio_test_src_name);
    GstElement *faac = gst_element_factory_make("avenc_aac", avenc_aac_name);
    g_free(audio_test_src_name);

    g_free(peer_id);

    if (!video_queue || !flvmux || !rtmpsink || !audio_test) {
        GST_ERROR_OBJECT(self, "Failed to create all elements.");
        return FALSE;
    }

    /** Add video_queue, flvmux and rtmpssink elements to the livepublisherbin. */
    gst_bin_add_many(self, flvmux, video_queue, rtmpsink, audio_test, faac, NULL);
    GST_INFO_OBJECT(self,
                    "flvmux, video_queue_name and rtmpsink elements are added with success to livepublisherbin.");

    /** Sync video_queue, flvmux and rtmpssink elements and link them to the livepublisherbin. */
    if (!gst_element_sync_state_with_parent(video_queue)) {
        ret = FALSE;
        GST_ERROR_OBJECT(self, "Failed to sync video_queue with parent.");
        goto gst_live_publisher_bin_start_stream_cleanup;
    }
    if (!gst_element_sync_state_with_parent(faac)) {
        ret = FALSE;
        GST_ERROR_OBJECT(self, "Failed to sync faac with parent.");
        goto gst_live_publisher_bin_start_stream_cleanup;
    }
    if (!gst_element_sync_state_with_parent(flvmux)) {
        ret = FALSE;
        GST_ERROR_OBJECT(self, "Failed to sync flvmux with parent.");
        goto gst_live_publisher_bin_start_stream_cleanup;
    }
    if (!gst_element_sync_state_with_parent(rtmpsink)) {
        ret = FALSE;
        GST_ERROR_OBJECT(self, "Failed to sync rtmpsink with parent.");
        goto gst_live_publisher_bin_start_stream_cleanup;
    }
    if (!gst_element_sync_state_with_parent(audio_test)) {
        ret = FALSE;
        GST_ERROR_OBJECT(self, "Failed to sync audio_test with parent.");
        goto gst_live_publisher_bin_start_stream_cleanup;
    }

    if (!gst_element_link( self->live_video_tee, video_queue)) {
        ret = FALSE;
        GST_WARNING_OBJECT(self, "Failed to link self->live_video_tee -> video_queue.");
        goto gst_live_publisher_bin_start_stream_cleanup;
    }
    if (!gst_element_link( audio_test, faac)) {
        ret = FALSE;
        GST_WARNING_OBJECT(self, "Failed to link audio_test -> faac.");
        goto gst_live_publisher_bin_start_stream_cleanup;
    }
    if (!gst_element_link_pads(video_queue, NULL, flvmux, "video"))
        GST_ERROR_OBJECT(self, "could not link video_queue to flvmux.");
    if (!gst_element_link_pads(faac, NULL, flvmux, "audio"))
        GST_ERROR_OBJECT(self, "could not link video_queue to flvmux.");

    if (!gst_element_link_many( flvmux, rtmpsink, NULL)) {
        ret = FALSE;
        GST_WARNING_OBJECT(self, "Failed to link self->live_video_tee -> flvmux -> rtmpsink.");
        goto gst_live_publisher_bin_start_stream_cleanup;
    }

    GST_INFO_OBJECT(self, "self->live_video_tee -> flvmux -> video_queue -> rtmpsink are linked with success.");
    g_warning("test9");

gst_live_publisher_bin_start_stream_cleanup:
    return ret;
}

static gboolean
gst_live_publisher_bin_stop_stream(Gstlivepublisherbin *self,
                                   gint id) {
    gchar *video_queue_name, *flvmux_name, *rtmpsink_name;
    gchar *peer_id;
    GstElement *rtmpsink, *flvmux, *video_queue;
    GstPad *live_video_tee_src_pad, *live_video_sink_pad;
    gboolean ret = TRUE;

    peer_id = g_strdup_printf("%d", id);

    /** Remove rtmpsink */
    rtmpsink_name = g_strdup_printf("rtmpsink-%s", peer_id);
    rtmpsink = gst_bin_get_by_name(GST_BIN (self), rtmpsink_name);
    if (!rtmpsink) {
        GST_WARNING_OBJECT(self,
                           "%s is not found in livepublisherbin.", rtmpsink_name);
        ret = FALSE;
        goto gst_live_publisher_bin_stop_stream_cleanup;
    }
    g_free(rtmpsink_name);
    gst_element_set_state (rtmpsink, GST_STATE_NULL);
    if (!gst_bin_remove(GST_BIN (self), rtmpsink)) {
        GST_WARNING_OBJECT(self, "Failed to remove rtmpsink.");
        ret = FALSE;
        goto gst_live_publisher_bin_stop_stream_cleanup;
    }

    /** Remove flvmux */
    flvmux_name = g_strdup_printf("flvmux-%s", peer_id);
    flvmux = gst_bin_get_by_name(GST_BIN (self),
                                 flvmux_name);
    if (!flvmux) {
        GST_WARNING_OBJECT(self, "Failed to get %s.", flvmux_name);
        g_free(flvmux_name);
        g_free(peer_id);
        ret = FALSE;
        goto gst_live_publisher_bin_stop_stream_cleanup;
    }
    g_free(flvmux_name);

    gst_element_set_state (flvmux, GST_STATE_NULL);
    if (!gst_bin_remove(GST_BIN (self), flvmux)) {
        GST_WARNING_OBJECT(self, "Failed to remove video_queue.");
        ret = FALSE;
        goto gst_live_publisher_bin_stop_stream_cleanup;
    }

    /** Remove video queue */
    video_queue_name = g_strdup_printf("live-video-queue-%s", peer_id);
    video_queue = gst_bin_get_by_name(GST_BIN (self),
                                      video_queue_name);
    if (!video_queue) {
        GST_WARNING_OBJECT(self, "Failed to get %s.", video_queue_name);
        g_free(video_queue_name);
        g_free(peer_id);
        ret = FALSE;
        goto gst_live_publisher_bin_stop_stream_cleanup;
    }

    live_video_sink_pad = gst_element_get_static_pad(video_queue, "sink");
    if (!live_video_sink_pad) {
        GST_WARNING_OBJECT(self, "Failed to get live_video_sink_pad.");
        ret = FALSE;
        goto gst_live_publisher_bin_stop_stream_cleanup;
    }

    live_video_tee_src_pad = gst_pad_get_peer(live_video_sink_pad);
    if (!live_video_tee_src_pad) {
        GST_WARNING_OBJECT(self, "Failed to get live_video_tee_src_pad.");
        ret = FALSE;
        goto gst_live_publisher_bin_stop_stream_cleanup;
    }
    gst_object_unref(GST_OBJECT(live_video_sink_pad));

    gst_element_set_state (video_queue, GST_STATE_NULL);
    if (!gst_bin_remove(GST_BIN (self), video_queue)) {
        GST_WARNING_OBJECT(self, "Failed to remove video_queue.");
        ret = FALSE;
        goto gst_live_publisher_bin_stop_stream_cleanup;
    }
    g_free(video_queue_name);

    /** Remove video queue */
    gst_element_release_request_pad(self->live_video_tee, live_video_tee_src_pad);
    gst_object_unref(GST_OBJECT(live_video_tee_src_pad));

    GST_INFO_OBJECT(self, "Streaming stopped successfully. Peer id:%d", peer_id);

gst_live_publisher_bin_stop_stream_cleanup:
    g_free(peer_id);
    return ret;
}

/** GObject vmethod implementations */

/** Initialize the livepublisherbin's class */
static void
gst_live_publisher_bin_class_init(GstlivepublisherbinClass *klass) {
    GObjectClass *gobject_class = (GObjectClass *) klass;
    GstElementClass *gstelement_class = (GstElementClass *) klass;

    gobject_class = (GObjectClass *) klass;
    gstelement_class = (GstElementClass *) klass;

    gobject_class->set_property = gst_live_publisher_bin_set_property;
    gobject_class->get_property = gst_live_publisher_bin_get_property;

    g_object_class_install_property(gobject_class, PROP_MAX_LIVE_SESSIONS,
                                    g_param_spec_uint("max-live-sessions", "Max live sessions",
                                                      "Max live sessions", 0, G_MAXUINT32,
                                                      DEFAULT_MAX_SESSIONS,
                                                      G_PARAM_READWRITE));
    gst_live_publisher_bin_signals[START_STREAM_SIGNAL] =
            g_signal_newv("start-stream", G_TYPE_FROM_CLASS(klass),
                          G_SIGNAL_RUN_LAST | G_SIGNAL_ACTION,
                          g_cclosure_new(G_CALLBACK(gst_live_publisher_bin_start_stream),
                                         NULL, NULL),
                          NULL, NULL, NULL,
                          G_TYPE_BOOLEAN, // Return TRUE in case of success, FALSE otherwise
                          4, (GType[4]) {G_TYPE_INT, G_TYPE_STRING, G_TYPE_STRING, G_TYPE_STRING});
    // Take url (string), key (string), username (string) and password (string) as input param
    gst_live_publisher_bin_signals[STOP_STREAM_SIGNAL] =
            g_signal_newv("stop-stream", G_TYPE_FROM_CLASS(klass),
                          G_SIGNAL_RUN_LAST | G_SIGNAL_ACTION,
                          g_cclosure_new(G_CALLBACK(gst_live_publisher_bin_stop_stream),
                                         NULL, NULL),
                          NULL, NULL, NULL,
                          G_TYPE_BOOLEAN, // Return TRUE in case of success, FALSE otherwise
                          1, (GType[1]) {G_TYPE_INT});

    gst_element_class_set_details_simple(gstelement_class,
                                         "Live publisher bin",
                                         "Live publisher bin",
                                         "Live publisher bin",
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
gst_live_publisher_bin_init(Gstlivepublisherbin *self) {

    gboolean ret = FALSE;
    self->max_sessions = DEFAULT_MAX_SESSIONS;

    self->live_video_tee = gst_element_factory_make("tee", "live_video_tee");
    if (!self->live_video_tee) {
        GST_ERROR_OBJECT(self, "Failed to create live_video_tee element.");
        return;
    }
    g_object_set(G_OBJECT (self->live_video_tee), "allow-not-linked", TRUE, NULL);

    self->live_audio_tee = gst_element_factory_make("tee", "live_audio_tee");
    if (!self->live_audio_tee) {
        GST_ERROR_OBJECT(self, "Failed to create live_audio_tee element.");
        return;
    }
    g_object_set(G_OBJECT (self->live_audio_tee), "allow-not-linked", TRUE, NULL);

    gst_bin_add_many(GST_BIN (self), self->live_video_tee,/*
                     /*self->live_audio_tee , */
                     NULL);

    GstPad *sink_video_pad = gst_element_get_static_pad(self->live_video_tee, "sink");
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

}

static void
gst_live_publisher_bin_set_property(GObject *object, guint prop_id,
                                    const GValue *value, GParamSpec *pspec) {
    Gstlivepublisherbin *self = GST_LIVE_PUBLISHER_BIN(object);

    switch (prop_id) {
        case PROP_MAX_LIVE_SESSIONS:
            self->max_sessions = g_value_get_uint(value);
            break;
        default:
            G_OBJECT_WARN_INVALID_PROPERTY_ID (object, prop_id, pspec);
            break;
    }
}

static void
gst_live_publisher_bin_get_property(GObject *object, guint prop_id,
                                    GValue *value, GParamSpec *pspec) {
    Gstlivepublisherbin *self = GST_LIVE_PUBLISHER_BIN(object);

    switch (prop_id) {
        case PROP_MAX_LIVE_SESSIONS:
            g_value_set_uint(value, self->max_sessions);
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
live_publisher_bin_init(GstPlugin *livepublisherbin) {
    GST_DEBUG_CATEGORY_INIT (gst_live_publisher_bin_debug, "livepublisherbin",
                             0, "A Gstreamer bin allowing to output an RTMP stream.");

    return GST_ELEMENT_REGISTER (livepublisherbin, livepublisherbin);
}

#ifndef PACKAGE
#define PACKAGE "livepublisherbin"
#endif

GST_PLUGIN_DEFINE (GST_VERSION_MAJOR,
                   GST_VERSION_MINOR,
                   livepublisherbin,
                   "livepublisherbin",
                   live_publisher_bin_init,
                   "", "LGPL", "GStreamer", "http://gstreamer.net/")