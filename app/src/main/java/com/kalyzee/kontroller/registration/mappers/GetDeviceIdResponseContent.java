package com.kalyzee.kontroller.registration.mappers;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetDeviceIdResponseContent {
    @JsonProperty("id")
    private String id;

    public GetDeviceIdResponseContent(@JsonProperty("id") String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
