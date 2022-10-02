package com.kalyzee.panel_connection_manager.exceptions;

public class InvalidResponseTypeException extends RuntimeException {
    public InvalidResponseTypeException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public InvalidResponseTypeException(String errorMessage) {
        super(errorMessage);
    }
}