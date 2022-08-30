package com.kalyzee.panel_connection_manager.mappers.network;

import com.google.gson.annotations.SerializedName;

public class IsConnectedResponseContent {

    @SerializedName("is_connected")
    private Boolean isConnected;

    public IsConnectedResponseContent(Boolean is_connected) {
        this.isConnected = is_connected;
    }

    public void setIsConnected(Boolean is_connected) {
        this.isConnected = is_connected;
    }

    public Boolean getIs_connected() {
        return isConnected;
    }
}
