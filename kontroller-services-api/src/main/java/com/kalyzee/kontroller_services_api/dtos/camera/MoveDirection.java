package com.kalyzee.kontroller_services_api.dtos.camera;


import com.google.gson.annotations.SerializedName;

public enum MoveDirection {

    @SerializedName("up")
    UP("up"),
    @SerializedName("down")
    DOWN("down"),
    @SerializedName("left")
    LEFT("left"),
    @SerializedName("right")
    RIGHT("right");

    private String moveDirection;

    private MoveDirection(String move_direction) {
        this.moveDirection = move_direction;
    }

    public String getString() {
        return moveDirection;
    }

    public static MoveDirection value(String action) {
        for (MoveDirection e : values()) {
            if (e.moveDirection.equals(action)) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }
}
