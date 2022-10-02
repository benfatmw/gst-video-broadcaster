package com.kalyzee.kontroller.registration.mappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kalyzee.panel_connection_manager.exceptions.InvalidRequestActionException;

public enum MessageAction {

    @JsonProperty("system/register")
    REGISTER("system/register"),
    @JsonProperty("system/id")
    GET_ID("system/id");

    private String action;

    private MessageAction(String action) {
        this.action = action;
    }

    public String getString() {
        return action;
    }

    @JsonValue
    public static MessageAction value(String action) {
        for (MessageAction e : values()) {
            if (e.action.equals(action)) {
                return e;
            }
        }
        throw new InvalidRequestActionException("Input action: " + action + " is not supported.");
    }
}
