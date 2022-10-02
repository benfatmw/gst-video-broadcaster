package com.kalyzee.kontroller_services_api.dtos.system.update.download;

public class DownloadStateChangedEvent {

    private int oldState;
    private int newState;

    public DownloadStateChangedEvent(int oldState, int newState) {
        this.oldState = oldState;
        this.newState = newState;
    }

    public int getOldState() {
        return oldState;
    }

    public void setOldState(int oldState) {
        this.oldState = oldState;
    }

    public int getNewState() {
        return newState;
    }

    public void setNewState(int newState) {
        this.newState = newState;
    }
}
