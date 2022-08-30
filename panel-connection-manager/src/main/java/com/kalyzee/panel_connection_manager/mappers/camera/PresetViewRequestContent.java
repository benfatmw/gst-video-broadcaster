package com.kalyzee.panel_connection_manager.mappers.camera;

import com.google.gson.annotations.SerializedName;

public class PresetViewRequestContent {

    @SerializedName("preset_id")
    private int presetId;

    public PresetViewRequestContent(int presetId) {
        this.presetId = presetId;
    }

    public int getPresetId() {
        return presetId;
    }

    public void setPresetId(int presetId) {
        this.presetId = presetId;
    }
}