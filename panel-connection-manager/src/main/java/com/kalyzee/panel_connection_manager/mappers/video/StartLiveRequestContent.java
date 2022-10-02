package com.kalyzee.panel_connection_manager.mappers.video;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kalyzee.kontroller_services_api.dtos.video.LiveProfile;

public class StartLiveRequestContent {
    @JsonProperty("live_profile")
    private LiveProfile liveProfile;

    public StartLiveRequestContent(@JsonProperty("live_profile") LiveProfile liveProfile) {
        this.liveProfile = liveProfile;
    }

    public LiveProfile getLiveProfile() {
        return liveProfile;
    }

    public void setLiveProfile(LiveProfile liveProfile) {
        this.liveProfile = liveProfile;
    }

}
