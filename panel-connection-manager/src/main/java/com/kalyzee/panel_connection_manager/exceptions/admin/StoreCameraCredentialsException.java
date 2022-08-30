package com.kalyzee.panel_connection_manager.exceptions.admin;

public class StoreCameraCredentialsException extends RuntimeException {
    public StoreCameraCredentialsException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public StoreCameraCredentialsException(String errorMessage) {
        super(errorMessage);
    }
}