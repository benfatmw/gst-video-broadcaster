package com.kalyzee.panel_connection_manager.mappers;

import com.google.gson.annotations.SerializedName;

public class ErrorResponseContent {

    @SerializedName("error_message")
    private String errorMessage;

    public ErrorResponseContent(String error_message) {
        this.errorMessage = error_message;
    }

    public void setErrorMessage(String error_message) {
        this.errorMessage = error_message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
