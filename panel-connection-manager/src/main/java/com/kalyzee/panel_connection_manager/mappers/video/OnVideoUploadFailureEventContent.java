package com.kalyzee.panel_connection_manager.mappers.video;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OnVideoUploadFailureEventContent {

    @JsonProperty("session_id")
    private int sessionId;
    @JsonProperty("error_message")
    private String errorMessage;
    @JsonProperty("error_code")
    private int errorCode;

    public OnVideoUploadFailureEventContent(@JsonProperty("session_id") int sessionId,
                                            @JsonProperty("error_message") String errorMessage,
                                            @JsonProperty("error_code") int errorCode) {
        this.sessionId = sessionId;
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
