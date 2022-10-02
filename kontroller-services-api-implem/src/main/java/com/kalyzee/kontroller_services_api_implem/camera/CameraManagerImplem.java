package com.kalyzee.kontroller_services_api_implem.camera;


import android.util.Log;

import com.kalyzee.kontroller_services_api.dtos.camera.MoveDirection;
import com.kalyzee.kontroller_services_api.dtos.camera.ZoomType;
import com.kalyzee.kontroller_services_api.exceptions.camera.MoveCameraFailureException;
import com.kalyzee.kontroller_services_api.exceptions.camera.MoveToPresetViewFailureException;
import com.kalyzee.kontroller_services_api.exceptions.camera.SetPresetViewFailureException;
import com.kalyzee.kontroller_services_api.exceptions.camera.StopMovingCameraFailureException;
import com.kalyzee.kontroller_services_api.exceptions.camera.StopZoomingFailureException;
import com.kalyzee.kontroller_services_api.exceptions.camera.ZoomFailureException;
import com.kalyzee.kontroller_services_api.interfaces.ContextChangedListener;
import com.kalyzee.kontroller_services_api.interfaces.camera.CameraManager;

public class CameraManagerImplem implements CameraManager {

    private static final String TAG = "CameraManagerImplem";

    private static final String MOVING_CAMERA = "Moving the camera. direction: ";
    private static final String STOP_MOVING_CAMERA = "Stop moving the camera.";
    private static final String ZOOMING = "Zooming, zoom type: ";
    private static final String STOP_ZOOMING = "Stop zooming.";
    private static final String SET_PRESET_VIEW = "Set preset view. preset id: ";
    private static final String MOVE_TO_PRESET_VIEW = "Move to preset view. preset id: ";

    @Override
    public void move(MoveDirection direction) throws MoveCameraFailureException {
        Log.i (TAG, MOVING_CAMERA + direction.getString());
    }

    @Override
    public void stopMoving() throws StopMovingCameraFailureException {
        Log.i (TAG, STOP_MOVING_CAMERA);
    }

    @Override
    public void zoom(ZoomType type) throws ZoomFailureException {
        Log.i (TAG, ZOOMING + type);
    }

    @Override
    public void stopZooming() throws StopZoomingFailureException {
        Log.i (TAG, STOP_ZOOMING);
    }

    @Override
    public void setPresetView(int presetId) throws SetPresetViewFailureException {
        Log.i (TAG, SET_PRESET_VIEW + presetId);
    }

    @Override
    public void moveToPresetView(int presetId) throws MoveToPresetViewFailureException {
        Log.i (TAG, MOVE_TO_PRESET_VIEW + presetId);
    }

    @Override
    public void registerContextChangedListener(ContextChangedListener listener) {

    }

    @Override
    public void unregisterContextChangedListener(ContextChangedListener listener) {

    }
}
