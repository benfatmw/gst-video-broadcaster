package com.kalyzee.kontroller_services_api.interfaces.system.update.install;


import com.kalyzee.kontroller_services_api.exceptions.system.update.install.InstallApkUpdateFailureException;
import com.kalyzee.kontroller_services_api.exceptions.system.update.install.InstallOsUpdateFailureException;

public interface IUpdateInstaller {

    void installOsUpdate(String osPackageLocation) throws InstallOsUpdateFailureException;

    void installApkUpdate(String packageName, String packageAbsolutePath) throws InstallApkUpdateFailureException;
}
