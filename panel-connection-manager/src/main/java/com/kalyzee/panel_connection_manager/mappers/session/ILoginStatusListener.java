package com.kalyzee.panel_connection_manager.mappers.session;

public interface ILoginStatusListener {
    void onLoginResult(boolean status);

    void onLogoutResult(boolean status);
}
