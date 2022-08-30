package com.kalyzee.kontroller_services_api.exceptions.system.update.install;

public class InstallOsUpdateFailureException extends RuntimeException {
    public InstallOsUpdateFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
