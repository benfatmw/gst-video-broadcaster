package com.kalyzee.kontroller_services_api.dtos.video;

import com.google.gson.annotations.SerializedName;

public enum UploadType {

    @SerializedName("FTP")
    FTP("FTP"),

    @SerializedName("SFTP")
    SFTP("SFTP");

    private String uploadType;

    private UploadType(String uploadType) {
        this.uploadType = uploadType;
    }

    public String getString() {
        return uploadType;
    }

    public static UploadType value(String type) {
        for (UploadType e : values()) {
            if (e.uploadType.equals(type)) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }
}
