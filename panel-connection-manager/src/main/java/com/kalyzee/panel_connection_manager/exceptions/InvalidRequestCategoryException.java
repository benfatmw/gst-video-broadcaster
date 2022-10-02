package com.kalyzee.panel_connection_manager.exceptions;

public class InvalidRequestCategoryException extends RuntimeException {
    public InvalidRequestCategoryException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public InvalidRequestCategoryException(String errorMessage) {
        super(errorMessage);
    }
}