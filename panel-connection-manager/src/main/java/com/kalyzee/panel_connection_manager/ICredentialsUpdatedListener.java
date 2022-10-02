package com.kalyzee.panel_connection_manager;

public interface ICredentialsUpdatedListener {
    void onUpdated(String panelUri, String certificate);
}
