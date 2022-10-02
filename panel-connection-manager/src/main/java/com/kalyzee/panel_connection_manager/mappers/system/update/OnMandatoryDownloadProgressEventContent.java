package com.kalyzee.panel_connection_manager.mappers.system.update;


import com.fasterxml.jackson.annotation.JsonProperty;

public class OnMandatoryDownloadProgressEventContent {

    @JsonProperty("session_id")
    private String sessionId;
    @JsonProperty("current_size")
    private long currentSize;
    @JsonProperty("total_size")
    private long totalSize;

    public OnMandatoryDownloadProgressEventContent(@JsonProperty("session_id") String sessionId,
                                                   @JsonProperty("current_size") long currentSize,
                                                   @JsonProperty("total_size") long totalSize) {
        this.sessionId = sessionId;
        this.currentSize = currentSize;
        this.totalSize = totalSize;
    }

    public long getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(long currentSize) {
        this.currentSize = currentSize;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
