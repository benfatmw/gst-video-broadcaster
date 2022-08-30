
package com.kalyzee.kontroller_services_api.dtos.video;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IceServer {

    @SerializedName("urls")
    @Expose
    public String[] urls;
    @SerializedName("username")
    @Expose
    public String username;
    @SerializedName("credential")
    @Expose
    public String credential;

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
