
package com.kalyzee.panel_connection_manager.mappers.video;


import com.fasterxml.jackson.annotation.JsonProperty;

public class IceServer {

    @JsonProperty("urls")
    private String[] urls;
    @JsonProperty("username")
    private String username;
    @JsonProperty("credential")
    private String credential;

    public IceServer(@JsonProperty("urls") String[] urls,
                     @JsonProperty("username") String username,
                     @JsonProperty("credential") String credential) {
        this.urls = urls;
        this.username = username;
        this.credential = credential;
    }

    public String[] getUrls() {
        return urls;
    }

    public void setUrls(String[] urls) {
        this.urls = urls;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }
}
