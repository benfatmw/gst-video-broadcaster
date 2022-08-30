package com.kalyzee.panel_connection_manager.mappers.video;

import com.google.gson.annotations.SerializedName;

public enum VideoAction {

    @SerializedName("start_record")
    START_RECORD ("start_record"),
    @SerializedName("stop_record")
    STOP_RECORD ("stop_record"),
    @SerializedName("start_live")
    START_LIVE ("start_live"),
    @SerializedName("stop_live")
    STOP_LIVE ("stop_live"),
    @SerializedName("start_vod")
    START_VOD ("start_vod"),
    @SerializedName("stop_vod")
    STOP_VOD ("stop_vod"),
    @SerializedName("switch_scene")
    SWITCH_SCENE ("switch_scene"),
    @SerializedName("on_video_context_updated")
    ON_VIDEO_CONTEXT_UPDATED ("on_video_context_updated"),
    @SerializedName("get_video_context")
    GET_VIDEO_CONTEXT ("get_video_context"),
    @SerializedName("create_webrtc_connection")
    CREATE_WEBRTC_CONNECTION ("create_webrtc_connection");

    private String videoAction;

    private VideoAction(String video_action) {
        this.videoAction = video_action;
    }

    public String getString() {
        return videoAction;
    }

    public static VideoAction value(String action) {
        for (VideoAction e : values()) {
            if (e.videoAction.equals(action)) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }
}
