package com.kalyzee.kontroller_services_api.exceptions.video;

public class StopRecordFailureException extends RuntimeException {
    public StopRecordFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
