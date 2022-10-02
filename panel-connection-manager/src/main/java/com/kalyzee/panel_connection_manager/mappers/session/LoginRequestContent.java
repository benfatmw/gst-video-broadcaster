package com.kalyzee.panel_connection_manager.mappers.session;


import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequestContent {

    @JsonProperty("camera_id")
    private String cameraId;
    @JsonProperty("certificate")
    private String certificate;

    public LoginRequestContent(@JsonProperty("camera_id") String cameraId,
                               @JsonProperty("certificate") String certificate) {
        this.cameraId = cameraId;
        this.certificate = certificate;
    }

    public void setCameraId(String camera_id) {
        this.cameraId = camera_id;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getCameraId() {
        return cameraId;
    }

    public String getCertificate() {
        return certificate;
    }
}
