package com.kalyzee.kontroller_services_api.dtos.camera;


import com.google.gson.annotations.SerializedName;

public enum ZoomType {
    @SerializedName("add")
    ADD("add"),
    @SerializedName("dec")
    DEC("dec");

    private String zoomType;

    private ZoomType(String zoom_type) {
        this.zoomType = zoom_type;
    }

    public String getString() {
        return zoomType;
    }

    public static ZoomType value(String action) {
        for (ZoomType e : values()) {
            if (e.zoomType.equals(action)) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }
}
