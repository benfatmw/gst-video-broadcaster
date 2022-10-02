package com.kalyzee.panel_connection_manager.exceptions;

public class InvalidRequestActionException extends RuntimeException {
    public InvalidRequestActionException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public InvalidRequestActionException(String errorMessage) {
        super(errorMessage);
    }
}