package com.kalyzee.panel_connection_manager.mappers.video;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum WebrtcSignallingAction {

    @JsonProperty("start")
    START("start"),
    @JsonProperty("sdp")
    SDP("sdp"),
    @JsonProperty("iceCandidate")
    ICE_CANDIDATE("iceCandidate"),
    @JsonProperty("stop")
    STOP("stop");

    private String webrtcSignallingAction;

    private WebrtcSignallingAction(String webrtcSignallingAction) {
        this.webrtcSignallingAction = webrtcSignallingAction;
    }

    public String getString() {
        return webrtcSignallingAction;
    }

    @JsonValue
    public static WebrtcSignallingAction value(String action) {
        for (WebrtcSignallingAction e : values()) {
            if (e.webrtcSignallingAction.equals(action)) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }
}
