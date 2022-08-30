package com.kalyzee.kontroller_services_api.dtos.system.update;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * The UpdateSessionModel class provides information on an active UpdateSession resource.
 */
@Entity
public class UpdateSessionModel {

    @PrimaryKey
    @SerializedName("session_id")
    @NonNull
    private String sessionId;
    @SerializedName("update_mode")
    @NonNull
    private final UpdateMode updateMode;
    @SerializedName("image_type")
    @NonNull
    private final ImageType imageType;
    @SerializedName("version_code")
    @NonNull
    private final int versionCode;
    @SerializedName("state")
    @NonNull
    private String state;
    @NonNull
    private String downloadSessionId;

    public UpdateSessionModel(UpdateMode updateMode, ImageType imageType, int versionCode) {
        this.updateMode = updateMode;
        this.imageType = imageType;
        this.versionCode = versionCode;
    }

    public UpdateMode getUpdateMode() {
        return updateMode;
    }

    public ImageType getImageType() {
        return imageType;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public String getState() {
        return state;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDownloadSessionId() {
        return downloadSessionId;
    }

    public void setSessionId(@NonNull String sessionId) {
        this.sessionId = sessionId;
    }

    public void setDownloadSessionId(@NonNull String downloadSessionId) {
        this.downloadSessionId = downloadSessionId;
    }
}
