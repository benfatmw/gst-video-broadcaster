package com.kalyzee.panel_connection_manager.mappers.camera;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kalyzee.panel_connection_manager.exceptions.InvalidRequestActionException;

public enum CameraAction {

    @JsonProperty("move")
    MOVE("move"),
    @JsonProperty("stop_moving")
    STOP_MOVING("stop_moving"),
    @JsonProperty("zoom")
    ZOOM("zoom"),
    @JsonProperty("stop_zooming")
    STOP_ZOOMING("stop_zooming"),
    @JsonProperty("set_preset_view")
    SET_PRESET_VIEW("set_preset_view"),
    @JsonProperty("move_to_preset_view")
    MOVE_TO_PRESET_VIEW("move_to_preset_view");

    private String cameraAction;

    private CameraAction(String cameraAction) {
        this.cameraAction = cameraAction;
    }

    public String getString() {
        return cameraAction;
    }

    @JsonValue
    public static CameraAction value(String action) {
        for (CameraAction e : values()) {
            if (e.cameraAction.equals(action)) {
                return e;
            }
        }
        throw new InvalidRequestActionException("Input action: " + action + " is not supported.");
    }
}
