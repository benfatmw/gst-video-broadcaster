package com.kalyzee.panel_connection_manager.mappers.system.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kalyzee.kontroller_services_api.dtos.system.update.ImageType;
import com.kalyzee.kontroller_services_api.dtos.system.update.UpdateDescriptor;

public class StartMandatoryDownloadRequestContent  extends UpdateDescriptor {

    public StartMandatoryDownloadRequestContent(@JsonProperty("image_type")ImageType imageType,
                                                @JsonProperty("version_code") int versionCode,
                                                @JsonProperty("url") String url,
                                                @JsonProperty("sha256_fingerprint") String sha256Fingerprint)  {
        super(imageType, versionCode, url, sha256Fingerprint);
    }
}
