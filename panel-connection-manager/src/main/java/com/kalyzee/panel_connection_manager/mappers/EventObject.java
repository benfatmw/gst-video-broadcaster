package com.kalyzee.panel_connection_manager.mappers;

import com.google.gson.annotations.SerializedName;

public class EventObject<T1, T2> {

    @SerializedName("category")
    private RequestCategory category;
    @SerializedName("action")
    private T1 action;
    @SerializedName("content")
    private T2 content;

    public EventObject(RequestCategory category, T1 action, T2 content) {
        this.category = category;
        this.action = action;
        this.content = content;
    }

    public void setCategory(RequestCategory category) {
        this.category = category;
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

    public T1 getAction() {
        return action;
    }

    public T2 getContent() {
        return content;
    }

}