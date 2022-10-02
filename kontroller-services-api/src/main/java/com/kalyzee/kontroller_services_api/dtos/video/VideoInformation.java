package com.kalyzee.kontroller_services_api.dtos.video;


import com.fasterxml.jackson.annotation.JsonProperty;

public class VideoInformation {

    @JsonProperty("title")
    private String title;
    @JsonProperty("video_id")
    private int videoId;
    @JsonProperty("record_started_at")
    private long recordStartedAt;
    @JsonProperty("record_stopped_at")
    private long recordStoppedAt;

    public VideoInformation(@JsonProperty("title") String title,
                            @JsonProperty("video_id") int videoId,
                            @JsonProperty("record_started_at") long recordStartedAt,
                            @JsonProperty("record_stopped_at") long recordStoppedAt) {
        this.title = title;
        this.videoId = videoId;
        this.recordStartedAt = recordStartedAt;
        this.recordStoppedAt = recordStoppedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }
}
