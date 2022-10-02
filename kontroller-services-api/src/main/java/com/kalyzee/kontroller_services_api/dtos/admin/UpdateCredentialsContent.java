package com.kalyzee.kontroller_services_api.dtos.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateCredentialsContent {

    @JsonProperty("panel_uri")
    private String panelUri;
    @JsonProperty("certificate")
    private String certificate;

    public UpdateCredentialsContent(@JsonProperty("panel_uri") String panelUri,
                                    @JsonProperty("certificate") String certificate) {
        this.panelUri = panelUri;
        this.certificate = certificate;
    }

    public String getPanelUri() {
        return panelUri;
    }

    public void setPanelUri(String panelUri) {
        this.panelUri = panelUri;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }
}
