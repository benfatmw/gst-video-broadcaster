/**
* CONFIDENTIAL and PROPRIETARY software of KALYZEE SAS.
* Copyright (c) 2018 Kalyzee
* All rights reserved
* This copyright notice MUST be reproduced on all authorized copies.
*
* Authors : - Wassim Ben Fatma   <wassim.benfatma@kalyzee.com>
*/
#include "camerasrcbin.h"

#ifdef HAVE_CONFIG_H
#include <config.h>
#endif


struct _GstCameraSrcBin {
    GstBin parent_instance;
    GstElement *rtspsrc;
    GstElement *rtpjitterbuffer;
    GstElement *rtph264depay;
    GstElement *h264parse;
    GstElement *rtph264pay;
};

/* properties */
enum {
    PROP_0,
    PROP_LOCATION
};

G_DEFINE_TYPE(GstCameraSrcBin, gst_camera_src_bin, GST_TYPE_BIN
);

static GstStaticPadTemplate src_video_factory =
        GST_STATIC_PAD_TEMPLATE(
                "video_src",
                GST_PAD_SINK,
                GST_PAD_ALWAYS,
                GST_STATIC_CAPS("application/x-rtp"));

static void gst_camera_src_bin_set_property(GObject *object,
                                            guint prop_id,
                                            const GValue *value,
                                            GParamSpec *pspec) {
    GstCameraSrcBin *self = GST_CAMERA_SRC_BIN(object);

    switch (prop_id) {
        case PROP_LOCATION:
            g_object_set(self->rtspsrc, "location", g_value_get_string(value), NULL);
            break;
        default:
            G_OBJECT_WARN_INVALID_PROPERTY_ID(object, prop_id, pspec);
            break;
    }
}

static void gst_camera_src_bin_get_property(GObject *object,
                                            guint prop_id,
                                            GValue *value,
                                            GParamSpec *pspec) {
    GstCameraSrcBin *self = GST_CAMERA_SRC_BIN(object);
    int fd;

    switch (prop_id) {
        case PROP_LOCATION:
            g_object_get(self->rtspsrc, "location", value, NULL);
            break;
        default:
            G_OBJECT_WARN_INVALID_PROPERTY_ID(object, prop_id, pspec);
            break;
    }
}

static void cb_new_rtspsrc_pad(GstElement *element, GstPad *pad, gpointer data) {
    gchar *name;
    GstCaps *caps;
    gchar *description;
    gint ret;
    GstCameraSrcBin *self = GST_ELEMENT(data);

    name = gst_pad_get_name(pad);
    caps = gst_pad_get_pad_template_caps(pad);
    description = gst_caps_to_string(caps);

    GST_INFO_OBJECT(self, "A new pad %s was created in rtspsrc element.%s\n", name, caps, ", ",
                    description, "\n");
    g_free(description);


    if (!gst_element_link_pads(element, name, self->rtpjitterbuffer, "sink")) {
        GST_ERROR_OBJECT(self, "Failed to link rtspsrc -> rtpjitterbuffer.\n");
    }
    if (!gst_element_link_many(self->rtpjitterbuffer, self->rtph264depay, self->h264parse,
                               self->rtph264pay, NULL)) {
        GST_ERROR_OBJECT(self,
                         "Failed to link rtpjitterbuffer -> rtph264depay -> h264parse -> rtph264pay.");
    }
    g_free(name);

}

static void gst_camera_src_bin_init(GstCameraSrcBin *self) {
    GstBin *bin = GST_BIN(self);
    GstElement *element = GST_ELEMENT(self);

    self->rtspsrc = gst_element_factory_make("rtspsrc", "rtspsrc_cam");
    self->rtpjitterbuffer = gst_element_factory_make("rtpjitterbuffer",
                                                     "rtpjitterbuffer_cam");
    self->rtph264depay = gst_element_factory_make("rtph264depay", "rtph264depay_cam");
    self->h264parse = gst_element_factory_make("h264parse", "h264parse_cam");
    self->rtph264pay = gst_element_factory_make("rtph264pay", "rtph264pay_cam");

    if (!self->rtpjitterbuffer || !self->rtph264depay || !self->h264parse || !self->rtph264pay) {
        GST_ERROR_OBJECT(self, "Failed to create all camerasrcbin elements.");
    }
    g_object_set(G_OBJECT (self->rtspsrc), "udp-buffer-size", 262144, NULL);
    g_object_set(G_OBJECT (self->rtph264pay), "config-interval", 1, "timestamp-offset", 0, NULL);
    g_object_set(G_OBJECT (self->rtpjitterbuffer), "mode", 0, NULL);
    g_signal_connect(self->rtspsrc, "pad-added", G_CALLBACK(cb_new_rtspsrc_pad), self);

    gst_bin_add_many(GST_BIN(self), self->rtspsrc, self->rtpjitterbuffer, self->rtph264depay, self->h264parse,
                     self->rtph264pay, NULL);

    GstPad *src_video_pad = gst_element_get_static_pad(self->rtph264pay, "src");
    int ret = gst_element_add_pad(element, gst_ghost_pad_new("video_src", src_video_pad));
    gst_object_unref(GST_OBJECT(src_video_pad));
    if (!ret) {
        GST_ERROR_OBJECT(self, "Failed to add video ghost pad to the bin");
    }
}

static void gst_camera_src_bin_finalize(GObject *self) {
    GstCameraSrcBin *bin = GST_CAMERA_SRC_BIN(self);
    G_OBJECT_CLASS(gst_camera_src_bin_parent_class)->finalize(self);
}

static void gst_camera_src_bin_class_init(GstCameraSrcBinClass *klass) {
    GstElementClass *element_class = GST_ELEMENT_CLASS(klass);

    GObjectClass *object_class = G_OBJECT_CLASS(klass);

    object_class->set_property = gst_camera_src_bin_set_property;
    object_class->get_property = gst_camera_src_bin_get_property;
    object_class->finalize = gst_camera_src_bin_finalize;

    gst_element_class_add_static_pad_template (element_class, &src_video_factory);

    g_object_class_install_property(object_class, PROP_LOCATION,
                                    g_param_spec_string("location", "Device",
                                                        "RTSP source location.",
                                                        "", G_PARAM_READWRITE |
                                                            G_PARAM_STATIC_STRINGS));

    gst_element_class_set_static_metadata(element_class,
                                          "Camera SRC",
                                          "Camera bin",
                                          "camera bin",
                                          "Wassim Ben Fatma <wassim.benfatma@kalyzee.com>");
}

gboolean gst_camera_src_bin_plugin_init(GstPlugin *plugin) {
    return gst_element_register(plugin, "camerasrcbin",
                                GST_RANK_NONE,
                                GST_TYPE_CAMERA_SRC_BIN);
}
