package com.kalyzee.panel_connection_manager.mappers.video;

import com.google.gson.annotations.SerializedName;

public class StopLiveResponseContent {
    @SerializedName("record_id")
    private int recordId;

    public StopLiveResponseContent(int recordId) {
        this.recordId = recordId;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }
}
