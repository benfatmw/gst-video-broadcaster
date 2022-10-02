package com.kalyzee.panel_connection_manager.mappers;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseObject<T> {

    @JsonProperty("type")
    private ResponseType type;
    @JsonProperty("correlation_id")
    private String correlationId;
    @JsonProperty("auth_token")
    private String authToken;
    @JsonProperty("content")
    private T content;

    public ResponseObject(@JsonProperty("type") ResponseType type,
                          @JsonProperty("correlation_id") String correlationId,
                          @JsonProperty("auth_token") String authToken,
                          @JsonProperty("content") T content) {
        this.type = type;
        this.correlationId = correlationId;
        this.authToken = authToken;
        this.content = content;
    }

    public void setType(ResponseType type) {
        this.type = type;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public T getContent() {
        return content;
    }

    public String getAuthToken() {
        return authToken;
    }

    public ResponseType getType() {
        return type;
    }
}
