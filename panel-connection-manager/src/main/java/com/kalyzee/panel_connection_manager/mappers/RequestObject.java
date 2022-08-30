package com.kalyzee.panel_connection_manager.mappers;

import com.google.gson.annotations.SerializedName;

public class RequestObject<T1, T2> {

    @SerializedName("correlation_id")
    private String correlationId;
    @SerializedName("category")
    private RequestCategory category;
    @SerializedName("action")
    private T1 action;
    @SerializedName("content")
    private T2 content;
    @SerializedName("auth_token")
    private String authToken;

    public RequestObject(RequestCategory category, T1 action, T2 content, String correlation_id, String auth_token) {
        this.category = category;
        this.correlationId = correlation_id;
        this.authToken = auth_token;
        this.action = action;
        this.content = content;
    }

    public void setCategory(RequestCategory category) {
        this.category = category;
    }

    public void setCorrelationId(String correlation_id) {
        this.correlationId = correlation_id;
    }

    public void setAuthToken(String auth_token) {
        this.authToken = auth_token;
    }

    public void setAction(T1 action) {
        this.action = action;
    }

    public void setContent(T2 content) {
        this.content = content;
    }

    public RequestCategory getCategory() {
        return category;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public T1 getAction() {
        return action;
    }

    public T2 getContent() {
        return content;
    }

}
