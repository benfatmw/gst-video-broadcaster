package com.kalyzee.panel_connection_manager.executors;

import com.kalyzee.kontroller_services_api.interfaces.ContextChangedListener;

import org.json.JSONException;
import org.json.JSONObject;

public interface PanelRequestsExecutor {

    public JSONObject execute(String action, Object action_content) throws JSONException;

    public void registerEventListener(ContextChangedListener listener);

    public void unregisterEventListener(ContextChangedListener listener);
}
