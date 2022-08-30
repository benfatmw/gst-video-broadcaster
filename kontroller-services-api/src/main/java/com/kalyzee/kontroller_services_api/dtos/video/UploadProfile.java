package com.kalyzee.kontroller_services_api.dtos.video;

import com.google.gson.annotations.SerializedName;

public class UploadProfile {

    @SerializedName("type")
    private String type;
    @SerializedName("host")
    private String host;
    @SerializedName("username")
    private String username;
    @SerializedName("password")
    private String password;
    @SerializedName("port")
    private int port;
    @SerializedName("absolute_path")
    private String absolutePath;
    @SerializedName("file_name")
    private String fileName;

    public UploadProfile(String type, String host, String username, String password, int port, String absolutePath, String fileName) {
        this.type = type;
        this.host = host;
        this.username = username;
        this.password = password;
        this.absolutePath = absolutePath;
        this.fileName = fileName;
        this.port = port;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
