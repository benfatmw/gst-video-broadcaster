package com.kalyzee.kontroller_services_api.dtos.video;


import com.fasterxml.jackson.annotation.JsonProperty;

public class UploadSessionContext {
    @JsonProperty("error_status")
    public int errorStatus;
    @JsonProperty("state")
    public String state;

    public UploadSessionContext(@JsonProperty("error_status") int errorStatus,
                                @JsonProperty("state") String state) {
        this.errorStatus = errorStatus;
        this.state = state;
    }

    public int getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(int errorStatus) {
        this.errorStatus = errorStatus;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
