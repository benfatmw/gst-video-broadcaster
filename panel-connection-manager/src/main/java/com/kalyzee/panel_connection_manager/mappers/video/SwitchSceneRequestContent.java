package com.kalyzee.panel_connection_manager.mappers.video;


import com.fasterxml.jackson.annotation.JsonProperty;

public class SwitchSceneRequestContent {

    @JsonProperty("id")
    private int id;

    public SwitchSceneRequestContent(@JsonProperty("id") int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
