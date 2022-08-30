package com.kalyzee.panel_connection_manager.mappers.camera;

import com.google.gson.annotations.SerializedName;
import com.kalyzee.kontroller_services_api.dtos.camera.ZoomType;

public class ZoomRequestContent {

    @SerializedName("type")
    private ZoomType type;

    public ZoomRequestContent(ZoomType type) {
        this.type = type;
    }

    public void setType(ZoomType type) {
        this.type = type;
    }

    public ZoomType getType() {
        return type;
    }
}
