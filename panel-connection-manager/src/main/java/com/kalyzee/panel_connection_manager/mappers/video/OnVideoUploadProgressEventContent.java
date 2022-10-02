package com.kalyzee.panel_connection_manager.mappers.video;


import com.fasterxml.jackson.annotation.JsonProperty;

public class OnVideoUploadProgressEventContent {

    @JsonProperty("session_id")
    private int sessionId;
    @JsonProperty("current_size")
    private long currentSize;
    @JsonProperty("total_size")
    private long totalSize;

    public OnVideoUploadProgressEventContent(@JsonProperty("session_id") int sessionId,
                                             @JsonProperty("current_size") long currentSize,
                                             @JsonProperty("total_size") long totalSize) {
        this.sessionId = sessionId;
        this.currentSize = currentSize;
        this.totalSize = totalSize;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
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
}
