package com.kalyzee.panel_connection_manager.mappers;

import com.google.gson.annotations.SerializedName;

public enum ResponseType {

    @SerializedName("SUCCESS")
    SUCCESS ("SUCCESS"),
    @SerializedName("ERROR")
    ERROR ("ERROR");

    private String responseType;

    private ResponseType(String response_type) {
        this.responseType = response_type;
    }

    public String getString() {
        return responseType;
    }

    public static ResponseType value(String type) {
        for (ResponseType e : values()) {
            if (e.responseType.equals(type)) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }
}
