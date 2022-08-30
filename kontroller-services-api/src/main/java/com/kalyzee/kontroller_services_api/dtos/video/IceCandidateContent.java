package com.kalyzee.kontroller_services_api.dtos.video;

import com.google.gson.annotations.SerializedName;

public class IceCandidateContent {

    @SerializedName("sdpMLineIndex")
    private int sdpMLineIndex;
    @SerializedName("candidate")
    private String candidate;
    @SerializedName("sdpMid")
    private String sdpMid;

    public IceCandidateContent(int sdpMLineIndex, String candidate, String sdpMid) {
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

