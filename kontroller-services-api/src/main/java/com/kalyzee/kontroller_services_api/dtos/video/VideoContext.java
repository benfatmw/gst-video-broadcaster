package com.kalyzee.kontroller_services_api.dtos.video;

import com.google.gson.annotations.SerializedName;

public class VideoContext {

    @SerializedName("is_in_live")
    private boolean isInLive;
    @SerializedName("is_recording")
    private boolean isRecording;
    @SerializedName("live_started_at")
    private long liveStartedAt;
    @SerializedName("record_started_at")
    private long recordStartedAt;
    @SerializedName("current_scene")
    private int currentScene;
    @SerializedName("current_date")
    private long currentDate;

    public VideoContext(boolean isInLive, boolean isRecording, long liveStartedAt, long recordStartedAt, int currentScene, long currentDate) {
        this.isInLive = isInLive;
        this.isRecording = isRecording;
        this.liveStartedAt = liveStartedAt;
        this.recordStartedAt = recordStartedAt;
        this.currentScene = currentScene;
        this.currentDate = currentDate;
    }

    public boolean isInLive() {
        return isInLive;
    }

    public boolean isRecording() {
        return isRecording;
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

    public long getCurrentDate() {
        return currentDate;
    }
    public void setInLive(boolean inLive) {
        isInLive = inLive;
    }

    public void setRecording(boolean recording) {
        isRecording = recording;
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

    public void setCurrentDate(long currentDate) {
        this.currentDate = currentDate;
    }
}
