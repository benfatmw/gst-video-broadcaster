package com.kalyzee.panel_connection_manager.mappers.system.update;

import com.google.gson.annotations.SerializedName;
import com.kalyzee.kontroller_services_api.dtos.system.update.ImageType;

public class StartMandatoryDownloadRequestContent {
    @SerializedName("image_type")
    private ImageType imageType;
    @SerializedName("version_code")
    private int versionCode;
    @SerializedName("url")
    private String url;
    @SerializedName("sha256_fingerprint")
    private String sha256Fingerprint;

    public StartMandatoryDownloadRequestContent(ImageType imageType, int versionCode, String url, String sha256Fingerprint) {
        this.imageType = imageType;
        this.versionCode = versionCode;
        this.url = url;
        this.sha256Fingerprint = sha256Fingerprint;
    }

    public ImageType getImageType() {
        return imageType;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSha256Fingerprint() {
        return sha256Fingerprint;
    }

    public void setSha256Fingerprint(String sha256Fingerprint) {
        this.sha256Fingerprint = sha256Fingerprint;
    }
}
