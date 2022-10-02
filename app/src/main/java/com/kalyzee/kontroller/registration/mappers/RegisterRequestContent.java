package com.kalyzee.kontroller.registration.mappers;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegisterRequestContent {
    @JsonProperty("certificate")
    private String certificate;
    @JsonProperty("panel_uri")
    private String panelUri;

    public RegisterRequestContent(@JsonProperty("certificate") String certificate,
                                  @JsonProperty("panel_uri") String panelUri) {
        this.certificate = certificate;
        this.panelUri = panelUri;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getPanelUri() {
        return panelUri;
    }

    public void setPanelUri(String panelUri) {
        this.panelUri = panelUri;
    }
}
