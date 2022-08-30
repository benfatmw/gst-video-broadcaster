package com.kalyzee.kontroller_services_api.dtos.video;

import com.google.gson.annotations.SerializedName;

public class RecordSessionContent {

    @SerializedName("title")
    private String title;
    @SerializedName("state")
    private String state;
    @SerializedName("record_started_at")
    private long recordStartedAt;
    @SerializedName("record_stopped_at")
    private long recordStoppedAt;

    public RecordSessionContent(String title, String state, long recordStartedAt, long recordStoppedAt) {
        this.title = title;
        this.state = state;
        this.recordStartedAt = recordStartedAt;
        this.recordStoppedAt = recordStoppedAt;
    }

    public RecordSessionContent() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getRecordStartedAt() {
        return recordStartedAt;
    }

    public void setRecordStartedAt(long record_started_at) {
        this.recordStartedAt = record_started_at;
    }

    public long getRecordStoppedAt() {
        return recordStoppedAt;
    }

    public void setRecordStoppedAt(long record_stopped_at) {
        this.recordStoppedAt = record_stopped_at;
    }
}
