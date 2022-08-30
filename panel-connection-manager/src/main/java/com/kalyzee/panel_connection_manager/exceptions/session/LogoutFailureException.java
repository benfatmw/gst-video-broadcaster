package com.kalyzee.panel_connection_manager.exceptions.session;

public class LogoutFailureException extends RuntimeException {
    public LogoutFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public LogoutFailureException(String errorMessage) {
        super(errorMessage);
    }
}
