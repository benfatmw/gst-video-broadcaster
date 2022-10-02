package com.kalyzee.kontroller.registration.mappers;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponseContent {
    @JsonProperty("status")
    private String status;

    public ErrorResponseContent( @JsonProperty("status") String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
