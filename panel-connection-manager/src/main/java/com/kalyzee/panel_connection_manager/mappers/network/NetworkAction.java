package com.kalyzee.panel_connection_manager.mappers.network;

import com.google.gson.annotations.SerializedName;

public enum NetworkAction {

    @SerializedName("get_information")
    GET_INFORMATION("get_information");

    private String networkAction;

    private NetworkAction(String network_action) {
        this.networkAction = network_action;
    }

    public String getString() {
        return networkAction;
    }

    public static NetworkAction value(String action) {
        for (NetworkAction e : values()) {
            if (e.networkAction.equals(action)) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }
}
