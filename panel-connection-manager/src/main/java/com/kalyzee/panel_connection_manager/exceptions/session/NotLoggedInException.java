package com.kalyzee.panel_connection_manager.exceptions.session;

public class NotLoggedInException extends RuntimeException {
    public NotLoggedInException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public NotLoggedInException(String errorMessage) {
        super(errorMessage);
    }
}
