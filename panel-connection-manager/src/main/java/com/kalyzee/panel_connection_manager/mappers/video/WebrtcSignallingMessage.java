package com.kalyzee.panel_connection_manager.mappers.video;

import com.google.gson.annotations.SerializedName;

public class WebrtcSignallingMessage<T> {
    @SerializedName("action")
    private WebrtcSignallingAction action;
    @SerializedName("correlation_id")
    private String correlationId;
    @SerializedName("content")
    private T content;

    public WebrtcSignallingMessage(WebrtcSignallingAction action, T content, String correlationId) {
        this.action = action;
        this.content = content;
        this.correlationId = correlationId;
    }

    public WebrtcSignallingAction getAction() {
        return action;
    }

    public void setAction(WebrtcSignallingAction action) {
        this.action = action;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}
