package com.kalyzee.panel_connection_manager.mappers.admin;

import com.google.gson.annotations.SerializedName;

public class RegisterRequestContent {

    @SerializedName("camera_id")
    private String cameraId;
    @SerializedName("password")
    private String password;
    @SerializedName("room_id")
    private String roomId;

    public RegisterRequestContent(String camera_id, String password, String room_id) {
        this.cameraId = camera_id;
        this.password = password;
        this.roomId = room_id;
    }

    public void setCameraId(String camera_id) {
        this.cameraId = camera_id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoomId(String room_id) {
        this.roomId = room_id;
    }

    public String getCameraId() {
        return cameraId;
    }

    public String getPassword() {
        return password;
    }

    public String getRoomId() {
        return roomId;
    }
}
