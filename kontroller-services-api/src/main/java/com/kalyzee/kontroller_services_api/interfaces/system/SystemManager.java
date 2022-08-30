package com.kalyzee.kontroller_services_api.interfaces.system;


import com.kalyzee.kontroller_services_api.dtos.system.SystemInformation;
import com.kalyzee.kontroller_services_api.exceptions.system.FactoryResetFailureException;
import com.kalyzee.kontroller_services_api.exceptions.system.GetSystemInformationException;
import com.kalyzee.kontroller_services_api.exceptions.system.RebootFailureException;
import com.kalyzee.kontroller_services_api.interfaces.ContextChangedListener;

public interface SystemManager {
    SystemInformation getSystemInfo() throws GetSystemInformationException;

    void reboot() throws RebootFailureException;

    void factoryReset() throws FactoryResetFailureException;

    void registerContextChangedListener(ContextChangedListener listener);

    void unregisterContextChangedListener(ContextChangedListener listener);
}
