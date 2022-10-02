package com.kalyzee.panel_connection_manager.exceptions.session;

public class InvalidLoginErrorCodeException extends RuntimeException {

    public InvalidLoginErrorCodeException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public InvalidLoginErrorCodeException(String errorMessage) {
        super(errorMessage);
    }
}
