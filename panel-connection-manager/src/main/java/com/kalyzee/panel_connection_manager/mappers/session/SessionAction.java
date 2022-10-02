package com.kalyzee.panel_connection_manager.mappers.session;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kalyzee.panel_connection_manager.exceptions.InvalidRequestActionException;

public enum SessionAction {

    @JsonProperty("login")
    LOGIN("login"),
    @JsonProperty("logout")
    LOGOUT("logout");

    private String sessionAction;

    private SessionAction(String sessionAction) {
        this.sessionAction = sessionAction;
    }

    public String getString() {
        return sessionAction;
    }

    @JsonValue
    public static SessionAction value(String action) {
        for (SessionAction e : values()) {
            if (e.sessionAction.equals(action)) {
                return e;
            }
        }
        throw new InvalidRequestActionException("Input action: " + action + " is not supported.");
    }

}
