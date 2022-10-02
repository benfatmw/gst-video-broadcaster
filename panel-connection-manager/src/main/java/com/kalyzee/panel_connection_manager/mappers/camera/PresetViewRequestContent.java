package com.kalyzee.panel_connection_manager.mappers.camera;


import com.fasterxml.jackson.annotation.JsonProperty;

public class PresetViewRequestContent {

    @JsonProperty("preset_id")
    private int presetId;

    public PresetViewRequestContent(@JsonProperty("preset_id") int presetId) {
        this.presetId = presetId;
    }

    public int getPresetId() {
        return presetId;
    }

    public void setPresetId(int presetId) {
        this.presetId = presetId;
    }
}
