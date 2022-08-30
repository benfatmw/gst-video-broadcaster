package com.kalyzee.kontroller_services_api.dtos.system;

import android.os.SystemClock;

import com.google.gson.annotations.SerializedName;

import java.util.TimeZone;

public class SystemInformation {

    @SerializedName("architecture")
    private String architecture;
    @SerializedName("os_version_name")
    private String osVersionName;
    @SerializedName("os_version_code")
    private int osVersionCode;
    @SerializedName("os_build_timestamp")
    private long osBuildTimestamp;
    @SerializedName("c_module_version")
    private String cModuleVersion;
    @SerializedName("java_module_version")
    private String javaModuleVersion;
    @SerializedName("aosp_patches_version")
    private String aospPatchesVersion;
    @SerializedName("camera_firmware_version")
    private String cameraFirmwareVersion;
    @SerializedName("hdmi_firmware_version")
    private String hdmiFirmwareVersion;
    @SerializedName("timezone_raw_offset")
    private int timezoneRawOffset = TimeZone.getDefault().getRawOffset();
    @SerializedName("elapsed_realtime")
    private long elapsedRealTime = SystemClock.elapsedRealtime();
    @SerializedName("current_time_in_ms")
    private long currentTimeInMs = System.currentTimeMillis();

    public SystemInformation(String architecture, String osVersionName, int osVersionCode,
                             long osBuildTimestamp, String cModuleVersion, String javaModuleVersion,
                             String aospPatchesVersion, String cameraFirmwareVersion, String hdmiFirmwareVersion) {
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

    public int getTimezoneRawOffset() {
        return timezoneRawOffset;
    }

    public void setTimezoneRawOffset(int timezoneRawOffset) {
        this.timezoneRawOffset = timezoneRawOffset;
    }

    public long getElapsedRealTime() {
        return elapsedRealTime;
    }

    public void setElapsedRealTime(long elapsedRealTime) {
        this.elapsedRealTime = elapsedRealTime;
    }

    public long getCurrentTimeInMs() {
        return currentTimeInMs;
    }

    public void setCurrentTimeInMs(long currentTimeInMs) {
        this.currentTimeInMs = currentTimeInMs;
    }

}