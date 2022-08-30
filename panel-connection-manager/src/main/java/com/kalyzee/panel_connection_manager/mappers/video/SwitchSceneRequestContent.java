package com.kalyzee.panel_connection_manager.mappers.video;

import com.google.gson.annotations.SerializedName;

public class SwitchSceneRequestContent {

    @SerializedName("id")
    private int id;

    public SwitchSceneRequestContent(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
