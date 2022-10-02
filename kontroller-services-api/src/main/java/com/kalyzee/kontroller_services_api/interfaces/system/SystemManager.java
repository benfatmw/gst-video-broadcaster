package com.kalyzee.kontroller_services_api.interfaces.system;


import com.kalyzee.kontroller_services_api.dtos.system.SystemContext;
import com.kalyzee.kontroller_services_api.dtos.system.SystemInformation;
import com.kalyzee.kontroller_services_api.exceptions.system.FactoryResetFailureException;
import com.kalyzee.kontroller_services_api.exceptions.system.GetSystemContextException;
import com.kalyzee.kontroller_services_api.exceptions.system.GetSystemInformationException;
import com.kalyzee.kontroller_services_api.exceptions.system.RebootFailureException;
import com.kalyzee.kontroller_services_api.exceptions.system.SetTimeFailureException;
import com.kalyzee.kontroller_services_api.exceptions.system.SetTimeZoneFailureException;
import com.kalyzee.kontroller_services_api.interfaces.ContextChangedListener;

public interface SystemManager {

    SystemInformation getSystemInfo() throws GetSystemInformationException;

    SystemContext getSystemContext() throws GetSystemContextException;

    void reboot() throws RebootFailureException;

    void factoryReset() throws FactoryResetFailureException;

    void setSystemTime(long millis) throws SetTimeFailureException;

    void setTimeZone(String timeZone) throws SetTimeZoneFailureException;

    void registerContextChangedListener(ContextChangedListener listener);

    void unregisterContextChangedListener(ContextChangedListener listener);
}
