package com.kalyzee.kontroller_services_api.dtos.system.update.download;


import static com.kalyzee.kontroller_services_api.dtos.system.update.download.DownloadSessionState.getStateInt;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.kalyzee.kontroller_services_api.interfaces.system.update.download.IDownloadStateListener;

import java.util.ArrayList;
import java.util.List;

/**
 * The DownloadSessionModel class provides information on an active DownloadSession resource.
 */
@Entity
public class DownloadSessionModel {
    @PrimaryKey
    @JsonProperty("session_id")
    @NonNull
    public String sessionId;
    @JsonProperty("url")
    @NonNull
    private String url;
    @JsonProperty("sha256_fingerprint")
    @NonNull
    private  String sha256Fingerprint;
    @JsonProperty("file_location")
    @NonNull
    private  String fileLocation;
    @JsonProperty("file_name")
    @NonNull
    private  String fileName;
    @JsonProperty("state")
    @NonNull
    private String state;

    @Ignore
    private final List<IDownloadStateListener> downloadStateListenersList = new ArrayList<>();

    @JsonCreator
    public DownloadSessionModel(@JsonProperty("url") String url,
                                @JsonProperty("sha256_fingerprint") String sha256Fingerprint,
                                @JsonProperty("file_location") String fileLocation,
                                @JsonProperty("file_name") String fileName) {
        this.url = url;
        this.sha256Fingerprint = sha256Fingerprint;
        this.fileLocation = fileLocation;
        this.fileName = fileName;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("sha256_fingerprint")
    public String getSha256Fingerprint() {
        return sha256Fingerprint;
    }

    @JsonProperty("file_location")
    public String getFileLocation() {
        return fileLocation;
    }

    @JsonProperty("state")
    public String getState() {
        return state;
    }

    @JsonSetter("state")
    public void setState(String newState) {
        for (IDownloadStateListener listener : downloadStateListenersList) {
            listener.stateChanged(new DownloadStateChangedEvent(getStateInt(this.state),
                    getStateInt(newState)));
        }
        this.state = newState;
    }

    @JsonProperty("session_id")
    public String getSessionId() {
        return sessionId;
    }

    @JsonSetter("session_id")
    public void setSessionId(@NonNull String sessionId) {
        this.sessionId = sessionId;
    }

    @JsonProperty("file_name")
    public String getFileName() {
        return fileName;
    }

    public void addStateListener(IDownloadStateListener listener) {
        downloadStateListenersList.add(listener);
    }

    public void removeStateListener(IDownloadStateListener listener) {
        downloadStateListenersList.remove(listener);
    }

    @JsonSetter("url")
    public void setUrl(@NonNull String url) {
        this.url = url;
    }

    @JsonProperty("sha256_fingerprint")
    public void setSha256Fingerprint(@NonNull String sha256Fingerprint) {
        this.sha256Fingerprint = sha256Fingerprint;
    }

    @JsonSetter("file_location")
    public void setFileLocation(@NonNull String fileLocation) {
        this.fileLocation = fileLocation;
    }

    @JsonProperty("file_name")
    public void setFileName(@NonNull String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(" Download Session: {" );
        result.append("sessionId: " + sessionId);
        result.append(", url: " + url);
        result.append(", sha256Fingerprint: " + sha256Fingerprint);
        result.append(", fileLocation: " + fileLocation);
        result.append(", fileName: " + fileName);
        result.append(", state: " + state);
        result.append("}");
        return result.toString();
    }
}
