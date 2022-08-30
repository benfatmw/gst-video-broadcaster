package com.kalyzee.panel_connection_manager.mappers.system.update;

import com.google.gson.annotations.SerializedName;

public class OnMandatoryUpdateReadyForApplicationEventContent {

    @SerializedName("session_id")
    private String sessionId;

    public OnMandatoryUpdateReadyForApplicationEventContent(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
