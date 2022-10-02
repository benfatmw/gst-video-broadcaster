package com.kalyzee.kontroller_services_api.dtos.video;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UploadType {

    @JsonProperty("FTP")
    FTP("FTP"),
    @JsonProperty("SFTP")
    SFTP("SFTP");

    private String uploadType;

    private UploadType(String uploadType) {
        this.uploadType = uploadType;
    }

    public String getString() {
        return uploadType;
    }

    @JsonValue
    public static UploadType value(String type) {
        for (UploadType e : values()) {
            if (e.uploadType.equals(type)) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }
}
