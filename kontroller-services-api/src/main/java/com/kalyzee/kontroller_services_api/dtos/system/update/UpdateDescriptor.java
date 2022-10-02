package com.kalyzee.kontroller_services_api.dtos.system.update;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateDescriptor {

    @JsonProperty("image_type")
    private ImageType imageType;
    @JsonProperty("version_code")
    private int versionCode;
    @JsonProperty("url")
    private String url;
    @JsonProperty("sha256_fingerprint")
    private String sha256Fingerprint;

    public UpdateDescriptor(@JsonProperty("image_type")ImageType imageType,
                            @JsonProperty("version_code") int versionCode,
                            @JsonProperty("url") String url,
                            @JsonProperty("sha256_fingerprint") String sha256Fingerprint) {
        this.imageType = imageType;
        this.versionCode = versionCode;
        this.url = url;
        this.sha256Fingerprint = sha256Fingerprint;
    }

    public ImageType getImageType() {
        return imageType;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public String getUrl() {
        return url;
    }

    public String getSha256Fingerprint() {
        return sha256Fingerprint;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSha256Fingerprint(String sha256Fingerprint) {
        this.sha256Fingerprint = sha256Fingerprint;
    }
}
