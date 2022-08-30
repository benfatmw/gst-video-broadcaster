package com.kalyzee.kontroller_services_api.exceptions.system.update.install;

public class InstallApkUpdateFailureException extends RuntimeException {
    public InstallApkUpdateFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
