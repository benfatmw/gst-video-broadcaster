package com.kalyzee.panel_connection_manager.mappers.camera;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kalyzee.kontroller_services_api.dtos.camera.MoveDirection;

public class MoveRequestContent {

    @JsonProperty("direction")
    private MoveDirection direction;

    public MoveRequestContent(@JsonProperty("direction") MoveDirection direction) {
        this.direction = direction;
    }

    public void setDirection(MoveDirection direction) {
        this.direction = direction;
    }

    public MoveDirection getDirection() {
        return direction;
    }
}
