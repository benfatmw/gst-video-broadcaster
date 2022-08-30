package com.kalyzee.panel_connection_manager.mappers.session;

import com.google.gson.annotations.SerializedName;

public class LoginRequestContent {

    @SerializedName("camera_id")
    private String cameraId;
    @SerializedName("password")
    private String password;

    public LoginRequestContent(String camera_id, String password) {
        this.cameraId = camera_id;
        this.password = password;
    }

    public void setCameraId(String camera_id) {
        this.cameraId = camera_id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCameraId() {
        return cameraId;
    }

    public String getPassword() {
        return password;
    }
}
