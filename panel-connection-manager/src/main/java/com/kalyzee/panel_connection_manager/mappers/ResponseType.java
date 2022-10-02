package com.kalyzee.panel_connection_manager.mappers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kalyzee.panel_connection_manager.exceptions.InvalidResponseTypeException;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public enum ResponseType {

    @JsonProperty("SUCCESS")
    SUCCESS ("SUCCESS"),
    @JsonProperty("ERROR")
    ERROR ("ERROR");

    private String responseType;

    private ResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getString() {
        return responseType;
    }

    @JsonValue
    public static ResponseType value(String type) {
        for (ResponseType e : values()) {
            if (e.responseType.equals(type)) {
                return e;
            }
        }
        throw new InvalidResponseTypeException("Response type: " + type + " is not supported.");
    }
}
