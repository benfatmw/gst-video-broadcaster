package com.kalyzee.kontroller_services_api.dtos.system.update;


import static com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionState.getStateInt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kalyzee.kontroller_services_api.interfaces.system.update.IUpdateStateListener;

import java.util.ArrayList;
import java.util.List;

/**
 * The UpdateSessionModel class provides information on an active UpdateSession resource.
 */
@Entity
public class UpdateSessionModel {

    @PrimaryKey
    @NonNull
    @JsonProperty("session_id")
    private String sessionId;
    @JsonProperty("update_mode")
    private final UpdateMode updateMode;
    @JsonProperty("image_type")
    private final ImageType imageType;
    @JsonProperty("version_code")
    private final int versionCode;
    @JsonProperty("start_time")
    @Nullable
    private String startTime = null;
    @JsonProperty("end_time")
    @Nullable
    private String endTime = null;
    @JsonProperty("state")
    @NonNull
    private String state;

    @NonNull
    private String downloadSessionId;

    @Ignore
    private final List<IUpdateStateListener> updateStateListenersList = new ArrayList<>();

    @Ignore
    public UpdateSessionModel(UpdateMode updateMode, ImageType imageType, int versionCode) {
        this.updateMode = updateMode;
        this.imageType = imageType;
        this.versionCode = versionCode;
    }

    @JsonCreator
    public UpdateSessionModel(UpdateMode updateMode, ImageType imageType, int versionCode,
                              String startTime, String endTime) {
        this.updateMode = updateMode;
        this.imageType = imageType;
        this.versionCode = versionCode;
        this.startTime = startTime;
        this.endTime = endTime;
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

    public void setState(String newState) {
        for (IUpdateStateListener listener : updateStateListenersList) {
            listener.stateChanged(sessionId, new UpdateStateChangedEvent(getStateInt(this.state), getStateInt(newState)));
        }
        this.state = newState;
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

    @Nullable
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(@Nullable String startTime) {
        this.startTime = startTime;
    }

    @Nullable
    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(@Nullable String endTime) {
        this.endTime = endTime;
    }

    public void addStateListener(IUpdateStateListener listener) {
        updateStateListenersList.add(listener);
    }

    public void removeStateListener(IUpdateStateListener listener) {
        updateStateListenersList.remove(listener);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(" Update Session: {");
        result.append("sessionId: " + sessionId);
        result.append(", updateMode: " + updateMode);
        result.append(", imageType: " + imageType);
        result.append(", versionCode: " + versionCode);
        result.append(", startTime: " + startTime);
        result.append(", endTime: " + endTime);
        result.append(", state: " + state);
        result.append("}");
        return result.toString();
    }
}
