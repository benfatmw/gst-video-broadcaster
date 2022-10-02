package com.kalyzee.panel_connection_manager.mappers.video;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WebrtcSignallingMessage<T> {
    @JsonProperty("action")
    private WebrtcSignallingAction action;
    @JsonProperty("correlation_id")
    private String correlationId;
    @JsonProperty("content")
    private T content;

    public WebrtcSignallingMessage(@JsonProperty("action") WebrtcSignallingAction action,
                                   @JsonProperty("content") T content,
                                   @JsonProperty("correlation_id") String correlationId) {
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
