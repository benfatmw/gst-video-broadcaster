package com.kalyzee.kontroller_services_api.dtos.camera;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ZoomType {
    @JsonProperty("add")
    ADD("add"),
    @JsonProperty("dec")
    DEC("dec");

    private String zoomType;

    private ZoomType(String zoomType) {
        this.zoomType = zoomType;
    }

    public String getString() {
        return zoomType;
    }

    @JsonValue
    public static ZoomType value(String action) {
        for (ZoomType e : values()) {
            if (e.zoomType.equals(action)) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }
}
