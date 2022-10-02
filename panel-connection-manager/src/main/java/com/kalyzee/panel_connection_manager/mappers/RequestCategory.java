package com.kalyzee.panel_connection_manager.mappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kalyzee.panel_connection_manager.exceptions.InvalidRequestCategoryException;

public enum RequestCategory {

    @JsonProperty("session")
    SESSION("session"),
    @JsonProperty("admin")
    ADMIN("admin"),
    @JsonProperty("video")
    VIDEO("video"),
    @JsonProperty("camera")
    CAMERA("camera"),
    @JsonProperty("publishing")
    PUBLISHING("publishing"),
    @JsonProperty("system")
    SYSTEM("system"),
    @JsonProperty("network")
    NETWORK("network");

    private String requestCategory;

    private RequestCategory(String requestCategory) {
        this.requestCategory = requestCategory;
    }

    public String getString() {
        return requestCategory;
    }

    @JsonValue
    public static RequestCategory value(@JsonProperty("category") String category) {
        for (RequestCategory e : values()) {
            if (e.requestCategory.equals(category)) {
                return e;
            }
        }
        throw new InvalidRequestCategoryException("Request category: " + category + " is not supported.");
    }
}
