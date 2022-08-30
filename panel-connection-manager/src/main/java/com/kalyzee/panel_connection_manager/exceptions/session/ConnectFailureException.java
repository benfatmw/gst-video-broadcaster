package com.kalyzee.panel_connection_manager.exceptions.session;

public class ConnectFailureException extends RuntimeException {
    public ConnectFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public ConnectFailureException(String errorMessage) {
        super(errorMessage);
    }
}
