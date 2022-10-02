package com.kalyzee.panel_connection_manager.mappers.system.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kalyzee.kontroller_services_api.dtos.system.update.ImageType;
import com.kalyzee.kontroller_services_api.dtos.system.update.silent.SilentUpdateDescriptor;

public class ScheduleSilentUpdateRequestContent extends SilentUpdateDescriptor {

    public ScheduleSilentUpdateRequestContent(@JsonProperty("image_type") ImageType imageType,
                                              @JsonProperty("version_code") int versionCode,
                                              @JsonProperty("url") String url,
                                              @JsonProperty("sha256_fingerprint") String sha256Fingerprint,
                                              @JsonProperty("start_time") String startTime,
                                              @JsonProperty("end_time") String endTime) {
        super(imageType, versionCode, url, sha256Fingerprint, startTime, endTime);
    }


}
