package com.kalyzee.panel_connection_manager.mappers.system.update;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InstallMandatoryUpdateRequestContent {
    @JsonProperty("session_id")
    private String sessionId;

    public InstallMandatoryUpdateRequestContent(@JsonProperty("session_id") String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
