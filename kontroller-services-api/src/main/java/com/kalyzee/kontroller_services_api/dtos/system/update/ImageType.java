package com.kalyzee.kontroller_services_api.dtos.system.update;

import com.google.gson.annotations.SerializedName;

public enum ImageType {
    @SerializedName("OS")
    OS("OS"),
    @SerializedName("APK")
    APK("APK");

    private String imageType;

    private ImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getString() {
        return imageType;
    }

    public static ImageType value(String action) {
        for (ImageType e : values()) {
            if (e.imageType.equals(action)) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }
}
