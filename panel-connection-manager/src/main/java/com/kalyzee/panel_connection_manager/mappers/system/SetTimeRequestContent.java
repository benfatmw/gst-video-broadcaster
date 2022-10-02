package com.kalyzee.panel_connection_manager.mappers.system;


import com.fasterxml.jackson.annotation.JsonProperty;

public class SetTimeRequestContent {
    @JsonProperty("time_in_ms")
    private long timeInMs;

    public SetTimeRequestContent(@JsonProperty("time_in_ms") long timeInMs) {
        this.timeInMs = timeInMs;
    }

    public long getTimeInMs() {
        return timeInMs;
    }

    public void setTimeInMs(long timeInMs) {
        this.timeInMs = timeInMs;
    }
}
