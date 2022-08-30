package com.kalyzee.panel_connection_manager.mappers.video;

import com.google.gson.annotations.SerializedName;
import com.kalyzee.kontroller_services_api.dtos.video.LiveProfile;
import com.kalyzee.kontroller_services_api.dtos.video.UploadProfile;

public class StartLiveRequestContent {
    @SerializedName("live_profile")
    private LiveProfile liveProfile;

    public StartLiveRequestContent(LiveProfile liveProfile, boolean isUploadEnabled, UploadProfile uploadProfile) {
        this.liveProfile = liveProfile;
    }

    public LiveProfile getLiveProfile() {
        return liveProfile;
    }

    public void setLiveProfile(LiveProfile liveProfile) {
        this.liveProfile = liveProfile;
    }

}
