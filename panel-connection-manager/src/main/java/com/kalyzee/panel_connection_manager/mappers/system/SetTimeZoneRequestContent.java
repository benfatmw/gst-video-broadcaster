package com.kalyzee.panel_connection_manager.mappers.system;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SetTimeZoneRequestContent {
    @JsonProperty("time_zone")
    private String timeZone;

    public SetTimeZoneRequestContent(@JsonProperty("time_zone") String timeZone) {
        this.timeZone = timeZone;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
