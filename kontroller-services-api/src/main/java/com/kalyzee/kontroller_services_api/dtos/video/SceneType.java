package com.kalyzee.kontroller_services_api.dtos.video;

import com.google.gson.annotations.SerializedName;

public enum SceneType {

    @SerializedName("add")
    ADD("add"),

    @SerializedName("dec")
    DEC("dec");

    private String sceneType;

    private SceneType(String sceneType) {
        this.sceneType = sceneType;
    }

    public String getString() {
        return sceneType;
    }

    public static SceneType value(String scene) {
        for (SceneType e : values()) {
            if (e.sceneType.equals(scene)) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }
}

