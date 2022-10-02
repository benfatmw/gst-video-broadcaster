package com.kalyzee.kontroller_services_api.exceptions.video;

public class GetRecordSessionContextFailureException extends RuntimeException  {
    public GetRecordSessionContextFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
