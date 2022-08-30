package com.kalyzee.kontroller_services_api.dtos.video;

import com.google.gson.annotations.SerializedName;

public class SdpContent {

    @SerializedName("type")
    private String type;
    @SerializedName("sdp")
    private String sdp;

    public SdpContent(String type, String sdp) {
        this.type = type;
        this.sdp = sdp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSdp() {
        return sdp;
    }

    public void setSdp(String sdp) {
        this.sdp = sdp;
    }
}