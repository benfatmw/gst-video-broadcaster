package com.kalyzee.kontroller.registration.mappers;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageObject<T1> {
    @JsonProperty("action")
    private MessageAction action;
    @JsonProperty("params")
    private T1 params;

    public MessageObject(@JsonProperty("action") MessageAction action,
                         @JsonProperty("params") T1 params) {
        this.action = action;
        this.params = params;
    }

    public MessageAction getAction() {
        return action;
    }

    public void setAction(MessageAction action) {
        this.action = action;
    }

    public T1 getParams() {
        return params;
    }

    public void setParams(T1 params) {
        this.params = params;
    }
}
