package com.kalyzee.panel_connection_manager.mappers.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kalyzee.panel_connection_manager.exceptions.InvalidRequestActionException;

public enum AdminAction {

    @JsonProperty("register")
    REGISTER("register"),
    @JsonProperty("update_credentials")
    UPDATE_CREDENTIALS("update_credentials");

    private String adminAction;

    private AdminAction(String adminAction) {
        this.adminAction = adminAction;
    }

    public String getString() {
        return adminAction;
    }

    @JsonValue
    public static AdminAction value(String action) {
        for (AdminAction e : values()) {
            if (e.adminAction.equals(action)) {
                return e;
            }
        }
        throw new InvalidRequestActionException("Input action: " + action + " is not supported.");
    }
}
