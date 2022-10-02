package com.kalyzee.panel_connection_manager.exceptions.session;

public class LoginConnectionFailureException extends RuntimeException {
    public LoginConnectionFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public LoginConnectionFailureException(String errorMessage) {
        super(errorMessage);
    }
}