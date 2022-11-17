/**
* CONFIDENTIAL and PROPRIETARY software of KALYZEE SAS.
* Copyright (c) 2022 Kalyzee
* All rights reserved
* This copyright notice MUST be reproduced on all authorized copies.
*
* Authors : - Wassim BEN FATMA  <wassim.benfatma@kalyzee.com>
*/
#ifndef KONTROLLER_GSTLIVEPUBLISHERBIN_H
#define KONTROLLER_GSTLIVEPUBLISHERBIN_H

#include <gst/gst.h>
#include <stdbool.h>

enum {
    PROP_MAX_LIVE_SESSIONS
};

enum {
    START_STREAM_SIGNAL = 0,
    STOP_STREAM_SIGNAL,
    LAST_LIVE_PUBLISHER_SIGNAL
};

G_BEGIN_DECLS

#define GST_TYPE_LIVEPUBLISHERBIN (gst_live_publisher_bin_get_type())

G_DECLARE_FINAL_TYPE(Gstlivepublisherbin, gst_live_publisher_bin,
                     GST, LIVE_PUBLISHER_BIN, GstBin)


struct _Gstlivepublisherbin {
    GstBin bin;
    guint max_sessions;
    GstElement *live_audio_tee;
    GstElement *live_video_tee;
};

int live_publisher_bin_init(GstPlugin *livepublisherkbin);

G_END_DECLS

#endif //KONTROLLER_GSTLIVEPUBLISHERBIN_H
