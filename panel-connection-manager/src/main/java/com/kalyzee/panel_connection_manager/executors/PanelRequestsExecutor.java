package com.kalyzee.panel_connection_manager.executors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kalyzee.kontroller_services_api.interfaces.ContextChangedListener;

import org.json.JSONException;
import org.json.JSONObject;

public interface PanelRequestsExecutor {

    JSONObject execute(String action, Object actionContent) throws JSONException, JsonProcessingException;

    void registerEventListener(ContextChangedListener listener);

    void unregisterEventListener(ContextChangedListener listener);
}
