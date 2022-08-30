package com.kalyzee.kontroller_services_api.dtos.system.update.download;

import androidx.annotation.NonNull;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;


/**
 * The DownloadSessionModel class provides information on an active DownloadSession resource.
 */
@Entity
public class DownloadSessionModel {
    @PrimaryKey
    @SerializedName("session_id")
    @NonNull
    public String sessionId;
    @SerializedName("url")
    @NonNull
    private final String url;
    @SerializedName("sha256_fingerprint")
    @NonNull
    private final String sha256Fingerprint;
    @SerializedName("file_location")
    @NonNull
    private final String fileLocation;
    @SerializedName("file_name")
    @NonNull
    private final String fileName;
    @SerializedName("state")
    @NonNull
    private String state;

    public DownloadSessionModel( String url, String sha256Fingerprint, String fileLocation, String fileName) {
        this.url = url;
        this.sha256Fingerprint = sha256Fingerprint;
        this.fileLocation = fileLocation;
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public String getSha256Fingerprint() {
        return sha256Fingerprint;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(@NonNull String sessionId) {
        this.sessionId = sessionId;
    }

    @NonNull
    public String getFileName() {
        return fileName;
    }
}
