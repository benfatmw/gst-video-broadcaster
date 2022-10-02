package com.kalyzee.kontroller_services_api.dtos.system;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SystemInformation {

    @JsonProperty("architecture")
    private String architecture;
    @JsonProperty("os_version_name")
    private String osVersionName;
    @JsonProperty("os_version_code")
    private int osVersionCode;
    @JsonProperty("os_build_timestamp")
    private long osBuildTimestamp;
    @JsonProperty("c_module_version")
    private String cModuleVersion;
    @JsonProperty("java_module_version")
    private String javaModuleVersion;
    @JsonProperty("aosp_patches_version")
    private String aospPatchesVersion;
    @JsonProperty("camera_firmware_version")
    private String cameraFirmwareVersion;
    @JsonProperty("hdmi_firmware_version")
    private String hdmiFirmwareVersion;

    @JsonCreator
    public SystemInformation(@JsonProperty("architecture") String architecture,
                             @JsonProperty("os_version_name") String osVersionName,
                             @JsonProperty("os_version_code") int osVersionCode,
                             @JsonProperty("os_build_timestamp") long osBuildTimestamp,
                             @JsonProperty("c_module_version") String cModuleVersion,
                             @JsonProperty("java_module_version") String javaModuleVersion,
                             @JsonProperty("aosp_patches_version") String aospPatchesVersion,
                             @JsonProperty("camera_firmware_version") String cameraFirmwareVersion,
                             @JsonProperty("hdmi_firmware_version") String hdmiFirmwareVersion) {
        this.architecture = architecture;
        this.osVersionName = osVersionName;
        this.osVersionCode = osVersionCode;
        this.osBuildTimestamp = osBuildTimestamp;
        this.cModuleVersion = cModuleVersion;
        this.javaModuleVersion = javaModuleVersion;
        this.aospPatchesVersion = aospPatchesVersion;
        this.cameraFirmwareVersion = cameraFirmwareVersion;
        this.hdmiFirmwareVersion = hdmiFirmwareVersion;
    }

    public String getOsVersionName() {
        return osVersionName;
    }

    public void setOsVersionName(String osVersionName) {
        this.osVersionName = osVersionName;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public String getCModuleVersion() {
        return cModuleVersion;
    }

    public void setCModuleVersion(String c_module_version) {
        this.cModuleVersion = c_module_version;
    }

    public String getJavaModuleVersion() {
        return javaModuleVersion;
    }

    public void setJavaModuleVersion(String java_module_version) {
        this.javaModuleVersion = java_module_version;
    }

    public long getOsBuildTimestamp() {
        return osBuildTimestamp;
    }

    public void setOsBuildTimestamp(long osBuildTimestamp) {
        this.osBuildTimestamp = osBuildTimestamp;
    }

    public String getcModuleVersion() {
        return cModuleVersion;
    }

    public void setcModuleVersion(String cModuleVersion) {
        this.cModuleVersion = cModuleVersion;
    }

    public int getOsVersionCode() {
        return osVersionCode;
    }

    public void setOsVersionCode(int osVersionCode) {
        this.osVersionCode = osVersionCode;
    }

    public String getAospPatchesVersion() {
        return aospPatchesVersion;
    }

    public void setAospPatchesVersion(String aospPatchesVersion) {
        this.aospPatchesVersion = aospPatchesVersion;
    }

    public String getCameraFirmwareVersion() {
        return cameraFirmwareVersion;
    }

    public void setCameraFirmwareVersion(String cameraFirmwareVersion) {
        this.cameraFirmwareVersion = cameraFirmwareVersion;
    }

    public String getHdmiFirmwareVersion() {
        return hdmiFirmwareVersion;
    }

    public void setHdmiFirmwareVersion(String hdmiFirmwareVersion) {
        this.hdmiFirmwareVersion = hdmiFirmwareVersion;
    }

}
