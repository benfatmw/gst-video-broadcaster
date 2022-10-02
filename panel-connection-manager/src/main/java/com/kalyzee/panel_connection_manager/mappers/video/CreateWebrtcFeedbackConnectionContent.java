package com.kalyzee.panel_connection_manager.mappers.video;


import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateWebrtcFeedbackConnectionContent {
    @JsonProperty("uri")
    private String uri;

    public CreateWebrtcFeedbackConnectionContent(@JsonProperty("uri") String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
