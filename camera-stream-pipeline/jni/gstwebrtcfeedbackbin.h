/**
* CONFIDENTIAL and PROPRIETARY software of KALYZEE SAS.
* Copyright (c) 2022 Kalyzee
* All rights reserved
* This copyright notice MUST be reproduced on all authorized copies.
*
* Authors : - Wassim BEN FATMA  <wassim.benfatma@kalyzee.com>
*/
#ifndef __GST_WEBRTCFEEDBACKBIN_H__
#define __GST_WEBRTCFEEDBACKBIN_H__

#include <gst/gst.h>
#include <stdbool.h>

enum {
    PROP_MAX_SESSIONS
};

enum {
    START_PREVIEW_SIGNAL = 0,
    STOP_PREVIEW_SIGNAL,
    ADD_ICE_CANDIDATE_SIGNAL,
    SET_REMOTE_DESCRIPTION_SIGNAL,
    ON_ICE_CANDIDATE_SIGNAL,
    ON_SDP_CREATED_SIGNAL,
    LAST_SIGNAL
};

G_BEGIN_DECLS

#define GST_TYPE_WEBRTCFEEDBACKBIN (gst_webrtc_feedback_bin_get_type())

G_DECLARE_FINAL_TYPE (Gstwebrtcfeedbackbin, gst_webrtc_feedback_bin,
                      GST, WEBRTC_FEEDBACK_BIN, GstBin)

typedef struct _ReceiverEntry {
    gint id;
    GstElement *webrtcbin;
    GMutex mutex;
} ReceiverEntry;

struct _Gstwebrtcfeedbackbin {
    GstBin bin;
    GstElement *webrtc_video_tee;
    GstElement *webrtc_audio_tee;
    guint max_webrtc_sessions;
    GHashTable *receiver_entry_table;
};

int webrtc_feedback_bin_init(GstPlugin *webrtcfeedbackbin);

G_END_DECLS

#endif /* __GST_WEBRTCFEEDBACKBIN_H__ */
