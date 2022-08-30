package com.kalyzee.kontroller_services_api_implem.system.update.install;

import com.kalyzee.kontroller_services_api.exceptions.system.update.install.InstallApkUpdateFailureException;
import com.kalyzee.kontroller_services_api.exceptions.system.update.install.InstallOsUpdateFailureException;
import com.kalyzee.kontroller_services_api.interfaces.system.update.install.IUpdateInstaller;

public class UpdateInstallerImplem implements IUpdateInstaller {

    @Override
    public void installOsUpdate(String osPackageLocation) throws InstallOsUpdateFailureException {

    }

    @Override
    public void installApkUpdate(String packageName, String packageAbsolutePath) throws InstallApkUpdateFailureException {

    }
}
