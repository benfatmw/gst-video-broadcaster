package com.kalyzee.panel_connection_manager.mappers.session;


import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponseContent {

    @JsonProperty("auth_token")
    private String authToken;

    public LoginResponseContent(@JsonProperty("auth_token") String authToken) {
        this.authToken = authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }
}
