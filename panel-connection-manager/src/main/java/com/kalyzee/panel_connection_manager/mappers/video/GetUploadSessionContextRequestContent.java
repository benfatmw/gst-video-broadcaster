package com.kalyzee.panel_connection_manager.mappers.video;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;


public class GetUploadSessionContextRequestContent {
    @JsonProperty("session_id")
    @NonNull
    private int sessionId;

    public GetUploadSessionContextRequestContent(@JsonProperty("session_id") int sessionId) {
        this.sessionId = sessionId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }
}
