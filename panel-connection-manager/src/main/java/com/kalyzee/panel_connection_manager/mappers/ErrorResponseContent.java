package com.kalyzee.panel_connection_manager.mappers;


import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponseContent {

    @JsonProperty("error_message")
    private String errorMessage;

    @JsonProperty("error_code")
    private int errorCode;

    public ErrorResponseContent(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ErrorResponseContent(@JsonProperty("error_message") String errorMessage,
                                @JsonProperty("error_code") int errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public void setErrorMessage(String error_message) {
        this.errorMessage = error_message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
