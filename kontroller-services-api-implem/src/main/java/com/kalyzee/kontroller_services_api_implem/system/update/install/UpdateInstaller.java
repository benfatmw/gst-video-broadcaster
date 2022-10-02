package com.kalyzee.kontroller_services_api_implem.system.update.install;

import android.content.Context;
import android.util.Log;

import com.kalyzee.kontroller_services_api.exceptions.system.update.install.InstallApkUpdateFailureException;
import com.kalyzee.kontroller_services_api.exceptions.system.update.install.InstallOsUpdateFailureException;
import com.kalyzee.kontroller_services_api.interfaces.system.update.install.IUpdateInstaller;

import org.apache.commons.lang3.NotImplementedException;

import java.io.File;

public class UpdateInstaller implements IUpdateInstaller {

    private static final String TAG = "UpdateInstaller";
    private static final String FAILED_TO_INSTALL_OS_UPDATE = "Failed to install OS update.";
    private static final String NOT_YET_SUPPORTED_ERROR = "Not yet supported.";

    private static final String ACTION_MASTER_CLEAR = "android.intent.action.MASTER_CLEAR";
    private static final String MOUNT_POINT = "mount_point";

    private Context context;

    public UpdateInstaller(Context context) {
        this.context = context;
    }

    @Override
    public void installOsUpdate(String osPackageLocation) throws InstallOsUpdateFailureException {
        Log.i(TAG, "Installation is not yet implemented.");
    }

    @Override
    public void installApkUpdate(String packageName, String packageAbsolutePath) throws InstallApkUpdateFailureException {
        throw new NotImplementedException(NOT_YET_SUPPORTED_ERROR);
    }

    private boolean checkUpdateFile(File updateFile) {
        if (updateFile.exists() && updateFile.canRead()) {
            return true;
        }
        return false;
    }
}
