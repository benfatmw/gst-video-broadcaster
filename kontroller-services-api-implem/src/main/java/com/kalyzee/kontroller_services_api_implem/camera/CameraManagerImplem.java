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
import com.kalyzee.visca_over_ip.ViscaCamera;

import java.io.IOException;

public class CameraManagerImplem implements CameraManager {

    private static final String TAG = "CameraManagerImplem";

    private static final String MOVING_CAMERA = "Moving the camera. direction: ";
    private static final String STOP_MOVING_CAMERA = "Stop moving the camera.";
    private static final String ZOOMING = "Zooming, zoom type: ";
    private static final String STOP_ZOOMING = "Stop zooming.";
    private static final String SET_PRESET_VIEW = "Set preset view. preset id: ";
    private static final String MOVE_TO_PRESET_VIEW = "Move to preset view. preset id: ";

    private static final String FAILED_TO_MOVE_CAMERA = "Failed to move camera.";
    private static final String FAILED_TO_STOP_MOVING_CAMERA = "Failed to stop moving camera.";
    private static final String FAILED_TO_ZOOM = "Failed to zoom.";
    private static final String FAILED_TO_STOP_ZOOMING = "Failed to stop zooming.";
    private static final String FAILED_TO_SET_PRESET_VIEW = "Failed to set preset view: ";
    private static final String FAILED_TO_MOVE_TO_PRESET_VIEW = "Failed to move to preset view: ";

    private final ViscaCamera viscaCamera;

    public CameraManagerImplem(ViscaCamera viscaCamera) {
        this.viscaCamera = viscaCamera;
    }

    @Override
    public void move(MoveDirection direction) throws MoveCameraFailureException {
        try {
            Log.i(TAG, MOVING_CAMERA + direction.getString());
            viscaCamera.move(direction);
        } catch (IOException e) {
            /** Catch exception and wrap it in #MoveCameraFailureException. */
            throw new MoveCameraFailureException(FAILED_TO_MOVE_CAMERA, e);
        }
    }

    @Override
    public void stopMoving() throws StopMovingCameraFailureException {
        try {
            Log.i(TAG, STOP_MOVING_CAMERA);
            viscaCamera.stopMoving();
        } catch (IOException e) {
            /** Catch exception and wrap it in #StopMovingCameraFailureException. */
            throw new StopMovingCameraFailureException(FAILED_TO_STOP_MOVING_CAMERA, e);
        }
    }

    @Override
    public void zoom(ZoomType type) throws ZoomFailureException {
        try {
            Log.i(TAG, ZOOMING + type);
            viscaCamera.zoom(type);
        } catch (IOException e) {
            /** Catch exception and wrap it in #ZoomFailureException. */
            throw new ZoomFailureException(FAILED_TO_ZOOM, e);
        }
    }

    @Override
    public void stopZooming() throws StopZoomingFailureException {
        try {
            Log.i(TAG, STOP_ZOOMING);
            viscaCamera.stopZooming();
        } catch (IOException e) {
            /** Catch exception and wrap it in #StopZoomingFailureException. */
            throw new StopZoomingFailureException(FAILED_TO_STOP_ZOOMING, e);
        }
    }

    @Override
    public void setPresetView(int presetId) throws SetPresetViewFailureException {

        try {
            Log.i(TAG, SET_PRESET_VIEW + presetId);
            viscaCamera.setPresetView(presetId);
        } catch (IOException e) {
            /** Catch exception and wrap it in #SetPresetViewFailureException. */
            throw new SetPresetViewFailureException(FAILED_TO_SET_PRESET_VIEW, e);
        }
    }

    @Override
    public void moveToPresetView(int presetId) throws MoveToPresetViewFailureException {
        try {
            Log.i (TAG, MOVE_TO_PRESET_VIEW + presetId);
            viscaCamera.moveToPresetView(presetId);
        } catch (Exception e) {
            /** Catch exception and wrap it in #MoveToPresetViewFailureException. */
            throw new MoveToPresetViewFailureException(FAILED_TO_MOVE_TO_PRESET_VIEW, e);
        }
    }

    @Override
    public void registerContextChangedListener(ContextChangedListener listener) {

    }

    @Override
    public void unregisterContextChangedListener(ContextChangedListener listener) {

    }
}
