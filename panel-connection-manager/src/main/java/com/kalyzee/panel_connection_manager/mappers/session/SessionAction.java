package com.kalyzee.panel_connection_manager.mappers.session;

import com.google.gson.annotations.SerializedName;

public enum SessionAction {

    @SerializedName("login")
    LOGIN("login"),
    @SerializedName("logout")
    LOGOUT("logout");

    private String sessionAction;

    private SessionAction(String session_action) {
        this.sessionAction = session_action;
    }

    public String getString() {
        return sessionAction;
    }

    public static SessionAction value(String action) {
        for (SessionAction e : values()) {
            if (e.sessionAction.equals(action)) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }

}
