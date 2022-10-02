package com.kalyzee.kontroller_services_api.dtos.video;


import com.fasterxml.jackson.annotation.JsonProperty;

public class UploadProfile {

    @JsonProperty("type")
    private String type;
    @JsonProperty("host")
    private String host;
    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;
    @JsonProperty("port")
    private int port;
    @JsonProperty("absolute_path")
    private String absolutePath;
    @JsonProperty("file_name")
    private String fileName;

    public UploadProfile(@JsonProperty("type") String type,
                         @JsonProperty("host") String host,
                         @JsonProperty("username") String username,
                         @JsonProperty("password") String password,
                         @JsonProperty("port") int port,
                         @JsonProperty("absolute_path") String absolutePath,
                         @JsonProperty("file_name") String fileName) {
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
