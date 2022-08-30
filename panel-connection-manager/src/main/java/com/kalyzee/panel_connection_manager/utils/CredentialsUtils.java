package com.kalyzee.panel_connection_manager.utils;

import android.content.SharedPreferences;

import com.kalyzee.panel_connection_manager.exceptions.admin.FetchCameraCredentialsException;
import com.kalyzee.panel_connection_manager.exceptions.admin.StoreCameraCredentialsException;


public class CredentialsUtils {

    private static final String TAG = "Credentials";

    private static final String PASSWORD = "password";
    private static final String CAMERA_ID = "camera_id";
    private static final String ORGANIZATION_ID = "organization_id";
    private static final String ROOM_ID = "room_id";
    private static final String PANEL_URI = "panel_uri";
    private static final String IS_REGISTERED = "is_registered";

    private static final String FAILED_TO_FETCH_CAMERA_CREDENTIALS = "Failed to fetch camera credentials.";
    private static final String FAILED_TO_STORE_CAMERA_CREDENTIALS = "Failed to store camera credentials.";
    private static final String NULL_INPUT_PARAMETERS = "Null input parameter.";
    private static final String CREDENTIAL_NOT_AVAILABLE = "Credentials not available in the store.";

    public static void storeCredentials(SharedPreferences pref, String panel_uri, String camera_id, String password, String room_id) {

        /** Sanity check */
        if ((pref == null) || (panel_uri == null) || (camera_id == null) || (password == null)) {
            throw new StoreCameraCredentialsException(NULL_INPUT_PARAMETERS);
        }

        SharedPreferences.Editor editor = pref.edit();
        /** Store the input credentials in the SharedPreference */
        try {
            editor.putString(PASSWORD, password);
            editor.putString(CAMERA_ID, camera_id);
            editor.putString(PANEL_URI, panel_uri);
            /** Room id is optional */
            if (room_id != null) {
                editor.putString(ROOM_ID, room_id);
            }
            editor.putBoolean(IS_REGISTERED, true);
            editor.apply();
        } catch (Exception e) {
            throw new StoreCameraCredentialsException(FAILED_TO_STORE_CAMERA_CREDENTIALS, e);
        }
    }

    public static String getRoomId(SharedPreferences pref) {
        /** Check if the room id has been already stored */
        if ((pref == null) || (!pref.contains(ROOM_ID))) {
            throw new FetchCameraCredentialsException(NULL_INPUT_PARAMETERS);
        }
        return pref.getString(ROOM_ID, null);
    }

    public static String getCameraId(SharedPreferences pref) {
        /** Check if the camera id has been already stored */
        if ((pref == null) || (!pref.contains(CAMERA_ID))) {
            throw new FetchCameraCredentialsException(CREDENTIAL_NOT_AVAILABLE);
        }
        return pref.getString(CAMERA_ID, null);
    }

    public static String getCameraPassword(SharedPreferences pref) {
        /** Check if the camera password has been already stored */
        if ((pref == null) || (!pref.contains(PASSWORD))) {
            throw new FetchCameraCredentialsException(CREDENTIAL_NOT_AVAILABLE);
        }
        return pref.getString(PASSWORD, null);
    }

    public static String getPanelUri(SharedPreferences pref) {
        /** Check if the Panel URI has been already stored */
        if ((pref == null) || (!pref.contains(PANEL_URI))) {
            throw new FetchCameraCredentialsException(CREDENTIAL_NOT_AVAILABLE);
        }
        return pref.getString(PANEL_URI, null);
    }

    public static boolean isCameraRegistered(SharedPreferences pref) {
        if ((pref == null) || (!pref.contains(IS_REGISTERED))) {
            return false;
        }
        return pref.getBoolean(IS_REGISTERED, false);
    }
}
