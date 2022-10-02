package com.kalyzee.kontroller_services_api.dtos.camera;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MoveDirection {

    @JsonProperty("up")
    UP("up"),
    @JsonProperty("down")
    DOWN("down"),
    @JsonProperty("left")
    LEFT("left"),
    @JsonProperty("right")
    RIGHT("right");

    private String moveDirection;

    private MoveDirection(String moveDirection) {
        this.moveDirection = moveDirection;
    }

    public String getString() {
        return moveDirection;
    }

    @JsonValue
    public static MoveDirection value(String action) {
        for (MoveDirection e : values()) {
            if (e.moveDirection.equals(action)) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }
}
