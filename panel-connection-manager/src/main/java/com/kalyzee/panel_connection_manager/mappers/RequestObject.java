package com.kalyzee.panel_connection_manager.mappers;


import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestObject<T1, T2> {

    @JsonProperty("correlation_id")
    private String correlationId;
    @JsonProperty("category")
    private RequestCategory category;
    @JsonProperty("action")
    private T1 action;
    @JsonProperty("content")
    private T2 content;
    @JsonProperty("auth_token")
    private String authToken;

    public RequestObject(@JsonProperty("category") RequestCategory category,
                         @JsonProperty("action") T1 action,
                         @JsonProperty("content") T2 content,
                         @JsonProperty("correlation_id") String correlationId,
                         @JsonProperty("auth_token") String auth_token) {
        this.category = category;
        this.correlationId = correlationId;
        this.authToken = auth_token;
        this.action = action;
        this.content = content;
    }

    public void setCategory(RequestCategory category) {
        this.category = category;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
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
