package com.kalyzee.panel_connection_manager.mappers.session;

import com.google.gson.annotations.SerializedName;

public class LoginResponseContent {

    @SerializedName("auth_token")
    private String authToken;

    public LoginResponseContent(String auth_token) {
        this.authToken = auth_token;
    }

    public void setAuthToken(String auth_token) {
        this.authToken = auth_token;
    }

    public String getAuthToken() {
        return authToken;
    }
}
