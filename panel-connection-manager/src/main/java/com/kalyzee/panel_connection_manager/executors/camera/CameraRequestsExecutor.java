package com.kalyzee.panel_connection_manager.executors.camera;


import static com.kalyzee.panel_connection_manager.mappers.ResponseType.ERROR;
import static com.kalyzee.panel_connection_manager.mappers.ResponseType.SUCCESS;

import com.google.gson.Gson;
import com.kalyzee.kontroller_services_api.interfaces.ContextChangedListener;
import com.kalyzee.kontroller_services_api.interfaces.camera.CameraManager;
import com.kalyzee.panel_connection_manager.executors.PanelRequestsExecutor;
import com.kalyzee.panel_connection_manager.mappers.ErrorResponseContent;
import com.kalyzee.panel_connection_manager.mappers.ResponseObject;
import com.kalyzee.panel_connection_manager.mappers.camera.CameraAction;
import com.kalyzee.panel_connection_manager.mappers.camera.MoveRequestContent;
import com.kalyzee.panel_connection_manager.mappers.camera.PresetViewRequestContent;
import com.kalyzee.panel_connection_manager.mappers.camera.ZoomRequestContent;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class CameraRequestsExecutor implements PanelRequestsExecutor {

    private CameraManager cameraService;

    public CameraRequestsExecutor(CameraManager cameraService) {
        this.cameraService = cameraService;
    }

    @Override
    public JSONObject execute(String action, Object actionContent) throws JSONException {
        Gson gson = new Gson();
        String gson_response;

        try {
            switch (CameraAction.value(action)) {
                case MOVE:
                    MoveRequestContent move_params = gson.fromJson(actionContent.toString(), MoveRequestContent.class);
                    cameraService.move(move_params.getDirection());
                    break;
                case STOP_MOVING:
                    cameraService.stopMoving();
                    break;
                case ZOOM:
                    ZoomRequestContent zoom_params = gson.fromJson(actionContent.toString(), ZoomRequestContent.class);
                    cameraService.zoom(zoom_params.getType());
                    break;
                case STOP_ZOOMING:
                    cameraService.stopZooming();
                    break;
                case SET_PRESET_VIEW:
                    PresetViewRequestContent set_preset_param = gson.fromJson(actionContent.toString(), PresetViewRequestContent.class);
                    cameraService.setPresetView(set_preset_param.getPresetId());
                    break;
                case MOVE_TO_PRESET_VIEW:
                    PresetViewRequestContent move_to_preset_param = gson.fromJson(actionContent.toString(), PresetViewRequestContent.class);
                    cameraService.moveToPresetView(move_to_preset_param.getPresetId());
                    break;
            }
            gson_response = gson.toJson(new ResponseObject<Object>(SUCCESS, null, null, null));
        } catch (Exception e) {
            gson_response = gson.toJson(new ResponseObject<ErrorResponseContent>(ERROR, null, null, new ErrorResponseContent(ExceptionUtils.getStackTrace(e))));
        }
        return new JSONObject(gson_response);
    }

    @Override
    public void registerEventListener(ContextChangedListener listener) {
        cameraService.registerContextChangedListener(listener);
    }

    @Override
    public void unregisterEventListener(ContextChangedListener listener) {
        cameraService.unregisterContextChangedListener(listener);
    }

}
