package com.kalyzee.panel_connection_manager.exceptions.session;

public class LoginFailureException extends RuntimeException {
    public LoginFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public LoginFailureException(String errorMessage) {
        super(errorMessage);
    }
}
