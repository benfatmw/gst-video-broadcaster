package com.kalyzee.kontroller_services_api.interfaces.system.update;


import com.kalyzee.kontroller_services_api.dtos.system.update.UpdateStateChangedEvent;

public interface IUpdateStateListener {
    void stateChanged(String sessionId, UpdateStateChangedEvent event);
    void cancel (String sessionId);
    void cancelAll();
}
