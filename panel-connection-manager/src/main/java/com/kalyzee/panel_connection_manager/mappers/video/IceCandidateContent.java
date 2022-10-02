package com.kalyzee.panel_connection_manager.mappers.video;


import com.fasterxml.jackson.annotation.JsonProperty;

public class IceCandidateContent {

    @JsonProperty("sdpMLineIndex")
    private int sdpMLineIndex;
    @JsonProperty("candidate")
    private String candidate;
    @JsonProperty("sdpMid")
    private String sdpMid;

    public IceCandidateContent(@JsonProperty("sdpMLineIndex") int sdpMLineIndex,
                               @JsonProperty("candidate") String candidate,
                               @JsonProperty("sdpMid") String sdpMid) {
        this.sdpMLineIndex = sdpMLineIndex;
        this.candidate = candidate;
        this.sdpMid = sdpMid;
    }

    public int getSdpMLineIndex() {
        return sdpMLineIndex;
    }

    public void setSdpMLineIndex(int sdpMLineIndex) {
        this.sdpMLineIndex = sdpMLineIndex;
    }

    public String getCandidate() {
        return candidate;
    }

    public void setCandidate(String candidate) {
        this.candidate = candidate;
    }

    public String getSdpMid() {
        return sdpMid;
    }

    public void setSdpMid(String sdpMid) {
        this.sdpMid = sdpMid;
    }
}

