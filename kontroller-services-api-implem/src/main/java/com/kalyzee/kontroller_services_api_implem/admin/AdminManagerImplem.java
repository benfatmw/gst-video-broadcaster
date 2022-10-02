package com.kalyzee.kontroller_services_api_implem.admin;


import com.kalyzee.kontroller_services_api.dtos.admin.UpdateCredentialsContent;
import com.kalyzee.kontroller_services_api.exceptions.admin.UpdateCredentialsException;
import com.kalyzee.kontroller_services_api.interfaces.admin.AdminManager;
import com.kalyzee.panel_connection_manager.CredentialsManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminManagerImplem implements AdminManager {

    private static final String TAG = "KastAdminManager";
    private static final String FAILED_TO_UPDATE_CREDENTIALS = "Failed to update credentials.";
    private static final String NULL_INPUT_PARAMETERS = "Failed to update credentials. Null input parameters.";

    private final CredentialsManager credentialsManager;
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    public AdminManagerImplem(CredentialsManager credentialsManager) {
        this.credentialsManager = credentialsManager;
    }

    @Override
    public void updateCredentials(UpdateCredentialsContent credentials) throws UpdateCredentialsException {
        try {
            if ((credentials == null)
                    || (credentials.getPanelUri() == null)
                    || (credentials.getCertificate() == null)) {
                throw new UpdateCredentialsException(NULL_INPUT_PARAMETERS);
            }
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    credentialsManager.setAllowFallback(true);
                    credentialsManager.storeCredentials(credentials.getPanelUri(), credentials.getCertificate());
                }
            });
        } catch (Exception e) {
            throw new UpdateCredentialsException(FAILED_TO_UPDATE_CREDENTIALS, e);
        }
    }
}
