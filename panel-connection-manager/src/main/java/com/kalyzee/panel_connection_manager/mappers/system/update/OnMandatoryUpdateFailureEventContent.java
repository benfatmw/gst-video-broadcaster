package com.kalyzee.panel_connection_manager.mappers.system.update;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OnMandatoryUpdateFailureEventContent {

    @JsonProperty("session_id")
    private String sessionId;
    @JsonProperty("error_message")
    private String errorMessage;

    public OnMandatoryUpdateFailureEventContent(@JsonProperty("session_id") String sessionId,
                                                @JsonProperty("error_message") String errorMessage) {
        this.sessionId = sessionId;
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
