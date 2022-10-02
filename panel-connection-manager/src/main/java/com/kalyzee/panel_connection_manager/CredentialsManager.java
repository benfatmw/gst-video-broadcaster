package com.kalyzee.panel_connection_manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.kalyzee.panel_connection_manager.exceptions.admin.FetchDeviceCredentialsException;
import com.kalyzee.panel_connection_manager.exceptions.admin.SaveBackupCredentialsFailure;
import com.kalyzee.panel_connection_manager.exceptions.admin.StoreDeviceCredentialsException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class CredentialsManager {

    private static final String TAG = "CredentialsManager";

    public static final String CERTIFICATE = "certificate";
    public static final String PANEL_URI = "panel_uri";
    public static final String IS_REGISTERED = "is_registered";
    public static final String ALLOW_FALLBACK = "allow_fallback";

    private static final String REGISTER_CREDENTIALS_UPDATED_LISTENER = "Register credentials updated listener.";
    private static final String UNREGISTER_CREDENTIALS_UPDATED_LISTENER = "Unregister credentials updated listener.";

    private static final String FAILED_TO_STORE_DEVICE_CREDENTIALS = "Failed to store device credentials.";
    private static final String NULL_INPUT_PARAMETERS = "Null input parameter.";
    private static final String CREDENTIALS_NOT_AVAILABLE = "Credentials not available in the store.";
    private static final String FAILED_TO_SAVE_FALLBACK_CREDENTIALS_TO_FILE = "Failed to save fallback credentials to file.";
    private static final String FAILED_TO_LOAD_FALLBACK_CREDENTIALS_FROM_FILE = "Failed to save fallback credentials to file.";
    private static final String FAILED_TO_STORE_REGISTRATION_STATUS = "Failed to store registration status.";
    private static final String FAILED_TO_SET_ALLOW_FALLBACK_ATTRIBUTE = "Failed to set allow fallback attribute.";
    private static final String FAILED_TO_CREATE_ENCRYPTED_SHARED_PREFERENCES
            = "Failed to create EncryptedSharedPreferences instance.";
    private static final String FAILED_DELETE_CREDENTIALS
            = "Failed to delete credentials file.";
    private static final String CREDENTIALS_DELETED_SUCCESSFULLY = "Credentials file deleted successfully";

    private static final String SHARED_PREFERENCE_DIR_NAME = "shared_prefs";

    private static final String RESTORE_FALLBACK_CREDENTIALS =
            "Empty credentials EncryptedSharedPreferences. Restored fallback credentials...";
    private static final String ATTEMPTING_TO_DELETE_CREDENTIALS_FILE = "Attempting to delete credentials file";
    private static final String FALLBACK_CREDENTIALS_PREFERENCE_LOCATION =
            Environment.getExternalStorageDirectory() + "/credentials/";
    private static final String FALLBACK_CREDENTIALS_FILENAME = "fallback_credentials.xml";

    private final String name;
    private final Context applicationContext;

    private SharedPreferences credentialsPreferences;
    private static List<ICredentialsUpdatedListener> credentialsUpdatedListenersList = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CredentialsManager(String name, Context applicationContext) {
        this.applicationContext = applicationContext;
        this.name = name;
        this.credentialsPreferences = createCredentialsPreferences();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public SharedPreferences createCredentialsPreferences() {
        SharedPreferences prefs = null;
        try {
            /**
             * If EncryptedSharedPreferences storing the device credentials doesn't exist
             * --> Load fallback credentials if there are any
             * Otherwise create an empty credentials EncryptedSharedPreferences
             and return (waiting for device registration)
             */
            File credentialsPreferenceFile = getSharedPreferencesPath();
            if (!credentialsPreferenceFile.exists()) {
                if (fallbackCredentialsExists()) {
                    Log.i(TAG, RESTORE_FALLBACK_CREDENTIALS);
                    restoreFallbackCredentials();
                }
            }
            prefs = EncryptedSharedPreferences.create(
                    name,
                    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                    applicationContext,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            Log.e(TAG, FAILED_TO_CREATE_ENCRYPTED_SHARED_PREFERENCES + e);
        }
        return prefs;
    }

    public void storeCredentials(String panelUri, String certificate) {

        /** Sanity check */
        if ((panelUri == null) || (certificate == null)) {
            throw new StoreDeviceCredentialsException(NULL_INPUT_PARAMETERS);
        }

        SharedPreferences.Editor editor = credentialsPreferences.edit();
        /** Store the input credentials in the SharedPreference */
        try {
            editor.putString(CERTIFICATE, certificate);
            editor.putString(PANEL_URI, panelUri);
            /**
             * commit() writes the data synchronously (blocking the thread its called from).
             * It then informs you about the success of the operation.
             */
            editor.commit();
        } catch (Exception e) {
            throw new StoreDeviceCredentialsException(FAILED_TO_STORE_DEVICE_CREDENTIALS, e);
        }
        notifyCredentialsUpdatedListeners(panelUri, certificate);
    }

    public String getDeviceCertificate() {
        /** Check if the device certificate has been already stored */
        if ((!credentialsPreferences.contains(CERTIFICATE))) {
            throw new FetchDeviceCredentialsException(CREDENTIALS_NOT_AVAILABLE);
        }
        return credentialsPreferences.getString(CERTIFICATE, null);
    }

    public String getPanelUri() {
        /** Check if the Panel URI has been already stored */
        if ((credentialsPreferences == null) || (!credentialsPreferences.contains(PANEL_URI))) {
            throw new FetchDeviceCredentialsException(CREDENTIALS_NOT_AVAILABLE);
        }
        return credentialsPreferences.getString(PANEL_URI, null);
    }

    public void setAllowFallback(boolean allowFallback) {
        SharedPreferences.Editor editor = credentialsPreferences.edit();
        /** Store the input registration status in the SharedPreference */
        try {
            editor.putBoolean(ALLOW_FALLBACK, allowFallback);
            /**
             * commit() writes the data synchronously (blocking the thread its called from).
             * It then informs you about the success of the operation.
             */
            editor.commit();
        } catch (Exception e) {
            throw new StoreDeviceCredentialsException(FAILED_TO_SET_ALLOW_FALLBACK_ATTRIBUTE, e);
        }
    }

    public boolean isFallbackAllowed() {
        /** Sanity check */
        if ((!credentialsPreferences.contains(ALLOW_FALLBACK))) {
            return false;
        }
        return credentialsPreferences.getBoolean(ALLOW_FALLBACK, false);
    }

    public void setDeviceRegistrationStatus(boolean isRegistered) {
        SharedPreferences.Editor editor = credentialsPreferences.edit();
        /** Store the input registration status in the SharedPreference */
        try {
            editor.putBoolean(IS_REGISTERED, isRegistered);
            /**
             * commit() writes the data synchronously (blocking the thread its called from).
             * It then informs you about the success of the operation.
             */
            editor.commit();
        } catch (Exception e) {
            throw new StoreDeviceCredentialsException(FAILED_TO_STORE_REGISTRATION_STATUS, e);
        }
    }

    public boolean isRegistered() {
        /** Sanity check */
        if ((!credentialsPreferences.contains(IS_REGISTERED))) {
            return false;
        }
        return credentialsPreferences.getBoolean(IS_REGISTERED, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void deleteCredentialsFile() {
        Log.i(TAG, ATTEMPTING_TO_DELETE_CREDENTIALS_FILE);
        if (getSharedPreferencesPath().delete()) {
            Log.i(TAG, CREDENTIALS_DELETED_SUCCESSFULLY);
        } else {
            Log.e(TAG, FAILED_DELETE_CREDENTIALS);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void saveFallbackCredentials() {
        try {
            /** Create fallback credentials directory if it doesn't exist */
            File credentialsDir = new File(FALLBACK_CREDENTIALS_PREFERENCE_LOCATION);
            if (!credentialsDir.exists()) {
                Log.w(TAG, "Directory " + credentialsDir.getAbsolutePath() + " doesn't exist. Create it.");
                credentialsDir.mkdir();
                credentialsDir.setWritable(true);
            }
            File fallbackCredentialsPreferencesFile =
                    new File(FALLBACK_CREDENTIALS_PREFERENCE_LOCATION + FALLBACK_CREDENTIALS_FILENAME);
            copy(getSharedPreferencesPath(), fallbackCredentialsPreferencesFile);
        } catch (Exception e) {
            throw new SaveBackupCredentialsFailure(FAILED_TO_SAVE_FALLBACK_CREDENTIALS_TO_FILE, e);
        }
    }

    public boolean fallbackCredentialsExists() {
        File fallbackCredentialsPreferencesFile =
                new File(FALLBACK_CREDENTIALS_PREFERENCE_LOCATION + FALLBACK_CREDENTIALS_FILENAME);
        return fallbackCredentialsPreferencesFile.exists();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void restoreFallbackCredentials() {
        try {
            File fallbackCredentialsPreferencesFile =
                    new File(FALLBACK_CREDENTIALS_PREFERENCE_LOCATION + FALLBACK_CREDENTIALS_FILENAME);
            copy(fallbackCredentialsPreferencesFile, getSharedPreferencesPath());
        } catch (Exception e) {
            throw new SaveBackupCredentialsFailure(FAILED_TO_LOAD_FALLBACK_CREDENTIALS_FROM_FILE, e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private File getSharedPreferencesPath() {
        /** Sanity checks */
        if (name.indexOf(File.separatorChar) >= 0) {
            throw new IllegalArgumentException("File " + name + " contains a path separator.");
        }
        File sharedPrefsDir = new File(applicationContext.getDataDir(), SHARED_PREFERENCE_DIR_NAME);
        if (!sharedPrefsDir.exists()) {
            Log.w(TAG, "Directory " + sharedPrefsDir.getAbsolutePath() + " doesn't exist. Create it.");
            sharedPrefsDir.mkdir();
        }
        return new File(sharedPrefsDir, name + ".xml");
    }

    private void copy(File source, File dest) throws Exception {
        /** If the source file doesn't exist throw #IllegalArgumentException */
        if (!source.exists()) {
            throw new IllegalArgumentException("File " + source.getAbsolutePath() + " doesn't exist.");
        }

        Log.i(TAG, "Attempting to copy " + source.getAbsolutePath() + " to " + dest.getAbsolutePath());

        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            destChannel.force(true);
        } finally {
            sourceChannel.close();
            destChannel.close();
        }
    }

    @SuppressWarnings("unchecked")
    public void registerCredentialsUpdatedListener(ICredentialsUpdatedListener listener) {
        Log.i(TAG, REGISTER_CREDENTIALS_UPDATED_LISTENER);
        credentialsUpdatedListenersList.add((ICredentialsUpdatedListener) listener);
    }

    @SuppressWarnings("unchecked")
    public void unregisterCredentialsUpdatedListener(ICredentialsUpdatedListener listener) {
        Log.i(TAG, UNREGISTER_CREDENTIALS_UPDATED_LISTENER);
        credentialsUpdatedListenersList.remove((ICredentialsUpdatedListener) listener);
    }

    private void notifyCredentialsUpdatedListeners(String panelUri, String certificate) {
        /** Iterating credentialsUpdatedListenersList ArrayList using Iterator */
        Iterator itr = credentialsUpdatedListenersList.iterator();
        while (itr.hasNext()) {
            @SuppressWarnings("unchecked")
            ICredentialsUpdatedListener listener = (ICredentialsUpdatedListener) itr.next();
            listener.onUpdated(Uri.encode(panelUri), certificate);
        }
    }

}
