package com.kalyzee.kontroller_services_api_implem.system;


import com.kalyzee.kontroller_services_api.dtos.system.SystemInformation;
import com.kalyzee.kontroller_services_api.exceptions.system.FactoryResetFailureException;
import com.kalyzee.kontroller_services_api.exceptions.system.GetSystemInformationException;
import com.kalyzee.kontroller_services_api.exceptions.system.RebootFailureException;
import com.kalyzee.kontroller_services_api.interfaces.ContextChangedListener;
import com.kalyzee.kontroller_services_api.interfaces.system.SystemManager;

public class SystemManagerImplem implements SystemManager {
    @Override
    public SystemInformation getSystemInfo() throws GetSystemInformationException {
        return null;
    }

    @Override
    public void reboot() throws RebootFailureException {

    }

    @Override
    public void factoryReset() throws FactoryResetFailureException {

    }

    @Override
    public void registerContextChangedListener(ContextChangedListener listener) {

    }

    @Override
    public void unregisterContextChangedListener(ContextChangedListener listener) {

    }
}
