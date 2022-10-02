package com.kalyzee.panel_connection_manager.mappers.network;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kalyzee.panel_connection_manager.exceptions.InvalidRequestActionException;

public enum NetworkAction {

    @JsonProperty("get_information")
    GET_INFORMATION("get_information");

    private String networkAction;

    private NetworkAction(String networkAction) {
        this.networkAction = networkAction;
    }

    public String getString() {
        return networkAction;
    }

    @JsonValue
    public static NetworkAction value(String action) {
        for (NetworkAction e : values()) {
            if (e.networkAction.equals(action)) {
                return e;
            }
        }
        throw new InvalidRequestActionException("Input action: " + action + " is not supported.");
    }
}
