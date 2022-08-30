package com.kalyzee.kontroller_services_api.dtos.video;

import com.google.gson.annotations.SerializedName;

public class CreateWebrtcFeedbackConnectionContent {
    @SerializedName("uri")
    private String uri;

    public CreateWebrtcFeedbackConnectionContent(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
