package com.kalyzee.panel_connection_manager.mappers.camera;

import com.google.gson.annotations.SerializedName;

public enum CameraAction {

    @SerializedName("move")
    MOVE("move"),
    @SerializedName("stop_moving")
    STOP_MOVING("stop_moving"),
    @SerializedName("zoom")
    ZOOM("zoom"),
    @SerializedName("stop_zooming")
    STOP_ZOOMING("stop_zooming"),
    @SerializedName("set_preset_view")
    SET_PRESET_VIEW("set_preset_view"),
    @SerializedName("move_to_preset_view")
    MOVE_TO_PRESET_VIEW("move_to_preset_view");

    private String cameraAction;

    private CameraAction(String camera_action) {
        this.cameraAction = camera_action;
    }

    public String getString() {
        return cameraAction;
    }

    public static CameraAction value(String action) {
        for (CameraAction e : values()) {
            if (e.cameraAction.equals(action)) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }
}
