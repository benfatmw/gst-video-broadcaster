package com.kalyzee.kontroller_services_api.dtos.system.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UpdateMode {
    @JsonProperty("MANDATORY")
    MANDATORY("MANDATORY"),
    @JsonProperty("SILENT")
    SILENT("SILENT");

    private String updateMode;

    private UpdateMode(String updateMode) {
        this.updateMode = updateMode;
    }

    public String getString() {
        return updateMode;
    }

    @JsonValue
    public static UpdateMode value(String action) {
        for (UpdateMode e : values()) {
            if (e.updateMode.equals(action)) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }
}
