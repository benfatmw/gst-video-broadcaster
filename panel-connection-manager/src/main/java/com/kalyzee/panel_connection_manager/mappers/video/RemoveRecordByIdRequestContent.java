package com.kalyzee.panel_connection_manager.mappers.video;


import com.fasterxml.jackson.annotation.JsonProperty;

public class RemoveRecordByIdRequestContent {
    @JsonProperty("video_id")
    private int videoId;

    public RemoveRecordByIdRequestContent(@JsonProperty("video_id") int videoId) {
        this.videoId = videoId;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }
}
