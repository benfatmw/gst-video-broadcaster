package com.kalyzee.kontroller_services_api.dtos.system.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ImageType {
    @JsonProperty("OS")
    OS("OS"),
    @JsonProperty("APK")
    APK("APK");

    private String imageType;

    private ImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getString() {
        return imageType;
    }

    @JsonValue
    public static ImageType value(String action) {
        for (ImageType e : values()) {
            if (e.imageType.equals(action)) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }
}
