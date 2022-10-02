package com.kalyzee.panel_connection_manager.mappers.camera;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kalyzee.kontroller_services_api.dtos.camera.ZoomType;

public class ZoomRequestContent {

    @JsonProperty("type")
    private ZoomType type;

    public ZoomRequestContent(@JsonProperty("type") ZoomType type) {
        this.type = type;
    }

    public void setType(ZoomType type) {
        this.type = type;
    }

    public ZoomType getType() {
        return type;
    }
}
