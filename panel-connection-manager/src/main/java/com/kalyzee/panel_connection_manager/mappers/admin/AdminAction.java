package com.kalyzee.panel_connection_manager.mappers.admin;

import com.google.gson.annotations.SerializedName;

public enum AdminAction {

    @SerializedName("register")
    REGISTER("register");

    private String adminAction;

    private AdminAction(String admin_action) {
        this.adminAction = admin_action;
    }

    public String getString() {
        return adminAction;
    }

    public static AdminAction value(String action) {
        for (AdminAction e : values()) {
            if (e.adminAction.equals(action)) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }
}
