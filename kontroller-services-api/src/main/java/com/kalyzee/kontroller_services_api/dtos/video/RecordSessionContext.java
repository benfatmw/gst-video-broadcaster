package com.kalyzee.kontroller_services_api.dtos.video;


import com.fasterxml.jackson.annotation.JsonProperty;

public class RecordSessionContext {

    @JsonProperty("video_information")
    public VideoInformation videoInformation;
    @JsonProperty("state")
    public String state;

    public RecordSessionContext(@JsonProperty("video_information") VideoInformation videoInformation,
                                @JsonProperty("state") String state) {
        this.videoInformation = videoInformation;
        this.state = state;
    }

    public VideoInformation getVideoInformation() {
        return videoInformation;
    }

    public void setVideoInformation(VideoInformation videoInformation) {
        this.videoInformation = videoInformation;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
