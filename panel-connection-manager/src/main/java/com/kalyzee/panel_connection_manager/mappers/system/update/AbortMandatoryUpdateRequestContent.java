package com.kalyzee.panel_connection_manager.mappers.system.update;

import com.google.gson.annotations.SerializedName;

public class AbortMandatoryUpdateRequestContent {
    @SerializedName("session_id")
    private String sessionId;

    public AbortMandatoryUpdateRequestContent(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
