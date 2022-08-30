package com.kalyzee.panel_connection_manager.mappers.video;

import com.google.gson.annotations.SerializedName;

public class StartRecordRequestContent {
    @SerializedName("title")
    private String title;

    public StartRecordRequestContent(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
