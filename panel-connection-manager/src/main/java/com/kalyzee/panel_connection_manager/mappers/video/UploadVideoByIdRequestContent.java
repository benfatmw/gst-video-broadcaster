package com.kalyzee.panel_connection_manager.mappers.video;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kalyzee.kontroller_services_api.dtos.video.UploadProfile;

public class UploadVideoByIdRequestContent {

    @JsonProperty("upload_profile")
    private UploadProfile uploadProfile;
    @JsonProperty("video_id")
    private int videoId;

    public UploadVideoByIdRequestContent(@JsonProperty("upload_profile") UploadProfile uploadProfile,
                                         @JsonProperty("video_id") int videoId) {
        this.uploadProfile = uploadProfile;
        this.videoId = videoId;
    }

    public UploadProfile getUploadProfile() {
        return uploadProfile;
    }

    public void setUploadProfile(UploadProfile uploadProfile) {
        this.uploadProfile = uploadProfile;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }
}
