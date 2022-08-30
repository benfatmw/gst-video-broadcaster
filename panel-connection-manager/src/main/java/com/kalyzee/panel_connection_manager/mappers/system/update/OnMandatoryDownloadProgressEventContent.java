package com.kalyzee.panel_connection_manager.mappers.system.update;

import com.google.gson.annotations.SerializedName;

public class OnMandatoryDownloadProgressEventContent {

    @SerializedName("session_id")
    private String sessionId;
    @SerializedName("current_size")
    private long currentSize;
    @SerializedName("total_size")
    private long totalSize;

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

    public OnMandatoryDownloadProgressEventContent(String sessionId, long currentSize, long totalSize) {
        this.sessionId = sessionId;
        this.currentSize = currentSize;
        this.totalSize = totalSize;
    }


    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
