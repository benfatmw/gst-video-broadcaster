package com.kalyzee.panel_connection_manager.mappers.video;

import com.google.gson.annotations.SerializedName;
import com.kalyzee.kontroller_services_api.dtos.video.UploadProfile;

public class StartVodRequestContent {

    @SerializedName("upload_profile")
    private UploadProfile uploadProfile;
    @SerializedName("video_id")
    private int videoId;

    public StartVodRequestContent(UploadProfile uploadProfile, int videoId) {
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
