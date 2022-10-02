package com.kalyzee.panel_connection_manager.exceptions.admin;

public class FetchDeviceCredentialsException extends RuntimeException {
    public FetchDeviceCredentialsException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public FetchDeviceCredentialsException(String errorMessage) {
        super(errorMessage);
    }
}
