package com.kalyzee.panel_connection_manager.exceptions.admin;

public class FetchCameraCredentialsException extends RuntimeException {
    public FetchCameraCredentialsException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public FetchCameraCredentialsException(String errorMessage) {
        super(errorMessage);
    }
}
