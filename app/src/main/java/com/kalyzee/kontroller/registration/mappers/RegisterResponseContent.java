package com.kalyzee.kontroller.registration.mappers;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegisterResponseContent {
    @JsonProperty("status")
    private String status;

    public RegisterResponseContent(@JsonProperty("status") String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
