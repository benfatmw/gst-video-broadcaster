package com.kalyzee.panel_connection_manager.mappers.system.update;

import com.google.gson.annotations.SerializedName;

public class OnMandatoryUpdateFailureEventContent {

    @SerializedName("session_id")
    private String sessionId;
    @SerializedName("error_message")
    private String errorMessage;

    public OnMandatoryUpdateFailureEventContent(String sessionId, String errorMessage) {
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
