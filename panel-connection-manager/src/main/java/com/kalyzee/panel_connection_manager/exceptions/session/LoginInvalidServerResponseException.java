package com.kalyzee.panel_connection_manager.exceptions.session;

public class LoginInvalidServerResponseException extends RuntimeException {
    public LoginInvalidServerResponseException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public LoginInvalidServerResponseException(String errorMessage) {
        super(errorMessage);
    }
}