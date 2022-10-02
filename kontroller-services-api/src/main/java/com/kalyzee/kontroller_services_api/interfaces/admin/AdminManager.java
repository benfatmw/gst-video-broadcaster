package com.kalyzee.kontroller_services_api.interfaces.admin;

import com.kalyzee.kontroller_services_api.dtos.admin.UpdateCredentialsContent;
import com.kalyzee.kontroller_services_api.exceptions.admin.UpdateCredentialsException;

public interface AdminManager {
    void updateCredentials(UpdateCredentialsContent credentials) throws UpdateCredentialsException;
}
