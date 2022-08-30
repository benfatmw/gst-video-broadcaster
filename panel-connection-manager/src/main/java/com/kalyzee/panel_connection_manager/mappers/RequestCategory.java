package com.kalyzee.panel_connection_manager.mappers;

import com.google.gson.annotations.SerializedName;

public enum RequestCategory {

    @SerializedName("session")
    SESSION ("session"),
    @SerializedName("admin")
    ADMIN("admin"),
    @SerializedName("video")
    VIDEO ("video"),
    @SerializedName("camera")
    CAMERA ("camera"),
    @SerializedName("publishing")
    PUBLISHING ("publishing"),
    @SerializedName("system")
    SYSTEM ("system"),
    @SerializedName("network")
    NETWORK ("network");

    private String requestCategory;

    private RequestCategory(String request_category) {
        this.requestCategory = request_category;
    }

    public String getString() {
        return requestCategory;
    }

    public static RequestCategory value(String category) {
        for (RequestCategory e : values()) {
            if (e.requestCategory.equals(category)) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }
}
