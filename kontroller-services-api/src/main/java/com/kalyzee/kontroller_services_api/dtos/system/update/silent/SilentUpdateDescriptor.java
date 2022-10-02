package com.kalyzee.kontroller_services_api.dtos.system.update.silent;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kalyzee.kontroller_services_api.dtos.system.update.ImageType;
import com.kalyzee.kontroller_services_api.dtos.system.update.UpdateDescriptor;

public class SilentUpdateDescriptor extends UpdateDescriptor {

    @JsonProperty("start_time")
    private String startTime;
    @JsonProperty("end_time")
    private String endTime;

    public SilentUpdateDescriptor(@JsonProperty("image_type") ImageType imageType,
                                  @JsonProperty("version_code") int versionCode,
                                  @JsonProperty("url") String url,
                                  @JsonProperty("sha256_fingerprint") String sha256Fingerprint,
                                  @JsonProperty("start_time") String startTime,
                                  @JsonProperty("end_time") String endTime){
        super(imageType, versionCode, url, sha256Fingerprint);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
