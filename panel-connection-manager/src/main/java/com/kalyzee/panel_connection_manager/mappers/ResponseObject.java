package com.kalyzee.panel_connection_manager.mappers;

import com.google.gson.annotations.SerializedName;

public class ResponseObject <T> {

    @SerializedName("type")
    private ResponseType type;
    @SerializedName("correlation_id")
    private String correlationId;
    @SerializedName("auth_token")
    private String authToken;
    @SerializedName("content")
    private T content;

    public ResponseObject(ResponseType type, String correlation_id, String auth_token, T content) {
        this.type = type;
        this.correlationId = correlation_id;
        this.authToken = auth_token;
        this.content = content;
    }

    public void setType(ResponseType type) {
        this.type = type;
    }

    public void setCorrelationId(String correlation_id) {
        this.correlationId = correlation_id;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public void setAuthToken(String auth_token) {
        this.authToken = auth_token;
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
