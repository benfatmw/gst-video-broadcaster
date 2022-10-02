package com.kalyzee.panel_connection_manager.exceptions.session;

public class LoginUnauthorizedAccessException extends RuntimeException {
    public LoginUnauthorizedAccessException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public LoginUnauthorizedAccessException(String errorMessage) {
        super(errorMessage);
    }
}
