/**
* CONFIDENTIAL and PROPRIETARY software of KALYZEE SAS.
* Copyright (c) 2018 Kalyzee
* All rights reserved
* This copyright notice MUST be reproduced on all authorized copies.
*
* Authors : - Ludovic Bouguerra   <ludovic.bouguerra@kalyzee.com>
*/
#ifndef GST_CAMERA_SRC_BIN_H
#define GST_CAMERA_SRC_BIN_H

#include <gst/gst.h>

G_BEGIN_DECLS

#define GST_TYPE_CAMERA_SRC_BIN gst_camera_src_bin_get_type ()
G_DECLARE_FINAL_TYPE (GstCameraSrcBin, gst_camera_src_bin, GST, CAMERA_SRC_BIN, GstBin)

struct _GstCameraSrcBinClass {
  GstBinClass parent_class;
};

gboolean gst_camera_src_bin_plugin_init (GstPlugin *plugin);

G_END_DECLS

#endif
