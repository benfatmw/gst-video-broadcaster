package com.kalyzee.panel_connection_manager.exceptions.admin;

public class RegisterFailureException extends RuntimeException {

    public RegisterFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public RegisterFailureException(String errorMessage) {
        super(errorMessage);
    }
}
