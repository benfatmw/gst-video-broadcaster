package com.kalyzee.kontroller_services_api.dtos.video;


import com.fasterxml.jackson.annotation.JsonProperty;

public class VideoContext {

    @JsonProperty("is_in_live")
    private boolean isInLive;
    @JsonProperty("live_started_at")
    private long liveStartedAt;
    @JsonProperty("record_state")
    private String recordState;
    @JsonProperty("record_started_at")
    private long recordStartedAt;
    @JsonProperty("record_stopped_at")
    private long recordStoppedAt;
    @JsonProperty("current_scene")
    private int currentScene;

    public VideoContext(@JsonProperty("is_in_live") boolean isInLive,
                        @JsonProperty("live_started_at") long liveStartedAt,
                        @JsonProperty("record_state") String recordState,
                        @JsonProperty("record_started_at") long recordStartedAt,
                        @JsonProperty("record_stopped_at") long recordStoppedAt,
                        @JsonProperty("current_scene") int currentScene) {
        this.isInLive = isInLive;
        this.liveStartedAt = liveStartedAt;
        this.recordState = recordState;
        this.recordStartedAt = recordStartedAt;
        this.recordStoppedAt = recordStoppedAt;
        this.currentScene = currentScene;
    }

    public boolean isInLive() {
        return isInLive;
    }


    public long getLiveStartedAt() {
        return liveStartedAt;
    }

    public long getRecordStartedAt() {
        return recordStartedAt;
    }

    public int getCurrentScene() {
        return currentScene;
    }

    public void setInLive(boolean inLive) {
        isInLive = inLive;
    }


    public void setLiveStartedAt(long liveStartedAt) {
        this.liveStartedAt = liveStartedAt;
    }

    public void setRecordStartedAt(long recordStartedAt) {
        this.recordStartedAt = recordStartedAt;
    }

    public void setCurrentScene(int currentScene) {
        this.currentScene = currentScene;
    }

    public String getRecordState() {
        return recordState;
    }

    public void setRecordState(String recordState) {
        this.recordState = recordState;
    }

    public long getRecordStoppedAt() {
        return recordStoppedAt;
    }

    public void setRecordStoppedAt(long recordStoppedAt) {
        this.recordStoppedAt = recordStoppedAt;
    }
}
