package com.kalyzee.panel_connection_manager.exceptions.session;

public class LoginServerInternalErrorException extends RuntimeException {
    public LoginServerInternalErrorException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public LoginServerInternalErrorException(String errorMessage) {
        super(errorMessage);
    }
}
