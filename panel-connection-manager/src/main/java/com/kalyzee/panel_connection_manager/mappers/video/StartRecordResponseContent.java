package com.kalyzee.panel_connection_manager.mappers.video;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StartRecordResponseContent {
    @JsonProperty("session_id")
    private int sessionId;

    public StartRecordResponseContent(@JsonProperty("session_id") int sessionId) {
        this.sessionId = sessionId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }
}
