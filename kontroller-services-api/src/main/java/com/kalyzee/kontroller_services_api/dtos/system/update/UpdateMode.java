package com.kalyzee.kontroller_services_api.dtos.system.update;

import com.google.gson.annotations.SerializedName;

public enum UpdateMode {
    @SerializedName("MANDATORY")
    MANDATORY("MANDATORY"),
    @SerializedName("SILENT")
    SILENT("SILENT");

    private String updateMode;

    private UpdateMode(String updateMode) {
        this.updateMode = updateMode;
    }

    public String getString() {
        return updateMode;
    }

    public static UpdateMode value(String action) {
        for (UpdateMode e : values()) {
            if (e.updateMode.equals(action)) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }
}
