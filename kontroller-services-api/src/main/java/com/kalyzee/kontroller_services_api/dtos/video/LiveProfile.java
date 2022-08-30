package com.kalyzee.kontroller_services_api.dtos.video;

import com.google.gson.annotations.SerializedName;

public class LiveProfile {

    @SerializedName("url")
    private String url;
    @SerializedName("username")
    private String username;
    @SerializedName("password")
    private String password;
    @SerializedName("key")
    private String key;

    public LiveProfile(String url, String username, String password, String key) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}