package com.kalyzee.kontroller_services_api.exceptions.network;

public class GetNetworkInformationException extends RuntimeException {
    public GetNetworkInformationException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
