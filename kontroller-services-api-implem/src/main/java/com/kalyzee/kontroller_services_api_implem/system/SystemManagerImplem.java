package com.kalyzee.kontroller_services_api_implem.system;


import android.util.Log;

import com.kalyzee.kontroller_services_api.dtos.system.SystemContext;
import com.kalyzee.kontroller_services_api.dtos.system.SystemInformation;
import com.kalyzee.kontroller_services_api.exceptions.system.FactoryResetFailureException;
import com.kalyzee.kontroller_services_api.exceptions.system.GetSystemContextException;
import com.kalyzee.kontroller_services_api.exceptions.system.GetSystemInformationException;
import com.kalyzee.kontroller_services_api.exceptions.system.RebootFailureException;
import com.kalyzee.kontroller_services_api.exceptions.system.SetTimeFailureException;
import com.kalyzee.kontroller_services_api.exceptions.system.SetTimeZoneFailureException;
import com.kalyzee.kontroller_services_api.interfaces.ContextChangedListener;
import com.kalyzee.kontroller_services_api.interfaces.system.SystemManager;

import java.util.ArrayList;
import java.util.List;

public class SystemManagerImplem implements SystemManager {

    private static final String TAG = "SystemManager";
    private static final String GET_SYSTEM_INFORMATION = "Get system information.";
    private static final String GET_SYSTEM_CONTEXT = "Get system context.";
    private static final String REBOOT = "Reboot the camera.";
    private static final String FACTORY_RESET = "Factory reset the camera.";
    private static final String SET_TIME = "Set system time: ";
    private static final String SET_TIMEZONE = "Set system timezone: ";
    private static final String REGISTER_CONTEXT_CHANGED_LISTENER = "Register context changed listener.";
    private static final String UNREGISTER_CONTEXT_CHANGED_LISTENER = "Unregister context changed listener.";

    private static List<ContextChangedListener<SystemContext>> systemContextListenersList = new ArrayList<>();

    @Override
    public SystemInformation getSystemInfo() throws GetSystemInformationException {
        Log.i(TAG, GET_SYSTEM_INFORMATION);
        return new SystemInformation("Stub", "Stub", 0,0,
                "Stub","Stub","Stub","Stub","Stub");
    }

    @Override
    public SystemContext getSystemContext() throws GetSystemContextException {
        Log.i(TAG, GET_SYSTEM_CONTEXT);
        return new SystemContext();
    }

    @Override
    public void reboot() throws RebootFailureException {
        Log.i(TAG, REBOOT);
    }

    @Override
    public void factoryReset() throws FactoryResetFailureException {
        Log.i(TAG, FACTORY_RESET);
    }

    @Override
    public void setSystemTime(long millis) throws SetTimeFailureException {
        Log.i(TAG, SET_TIME + millis);
    }

    @Override
    public void setTimeZone(String timeZone) throws SetTimeZoneFailureException {
        Log.i(TAG, SET_TIMEZONE + timeZone);
    }

    @Override
    public void registerContextChangedListener(ContextChangedListener listener) {
        Log.i(TAG, REGISTER_CONTEXT_CHANGED_LISTENER);
        systemContextListenersList.add((ContextChangedListener<SystemContext>) listener);
    }

    @Override
    public void unregisterContextChangedListener(ContextChangedListener listener) {
        Log.i(TAG, UNREGISTER_CONTEXT_CHANGED_LISTENER);
        systemContextListenersList.remove((ContextChangedListener<SystemContext>) listener);
    }
}
