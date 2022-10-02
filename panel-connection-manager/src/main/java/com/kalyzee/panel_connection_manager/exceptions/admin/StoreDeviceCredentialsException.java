package com.kalyzee.panel_connection_manager.exceptions.admin;

public class StoreDeviceCredentialsException extends RuntimeException {
    public StoreDeviceCredentialsException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public StoreDeviceCredentialsException(String errorMessage) {
        super(errorMessage);
    }
}