package com.kalyzee.panel_connection_manager.mappers.video;

import com.google.gson.annotations.SerializedName;

public enum WebrtcSignallingAction {

    @SerializedName("start")
    START("start"),
    @SerializedName("sdp")
    SDP("sdp"),
    @SerializedName("iceCandidate")
    ICE_CANDIDATE("iceCandidate"),
    @SerializedName("stop")
    STOP("stop");

    private String webrtcSignallingAction;

    private WebrtcSignallingAction(String webrtcSignallingAction) {
        this.webrtcSignallingAction = webrtcSignallingAction;
    }

    public String getString() {
        return webrtcSignallingAction;
    }

    public static WebrtcSignallingAction value(String action) {
        for (WebrtcSignallingAction e : values()) {
            if (e.webrtcSignallingAction.equals(action)) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }
}
