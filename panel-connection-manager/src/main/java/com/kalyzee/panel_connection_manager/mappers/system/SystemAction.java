package com.kalyzee.panel_connection_manager.mappers.system;

import com.google.gson.annotations.SerializedName;

public enum SystemAction {

    @SerializedName("get_system_information")
    GET_SYSTEM_INFORMATION("get_system_information"),
    @SerializedName("get_software_update_context")
    GET_SOFTWARE_UPDATE_CONTEXT("get_software_update_context"),
    @SerializedName("start_mandatory_software_download")
    START_MANDATORY_SOFTWARE_DOWNLOAD("start_mandatory_software_download"),
    @SerializedName("install_mandatory_software_update")
    INSTALL_MANDATORY_SOFTWARE_UPDATE("install_mandatory_software_update"),
    @SerializedName("abort_mandatory_software_update")
    ABORT_MANDATORY_SOFTWARE_UPDATE("abort_mandatory_software_update"),
    @SerializedName("on_mandatory_software_download_progress")
    ON_MANDATORY_SOFTWARE_DOWNLOAD_PROGRESS("on_mandatory_software_download_progress"),
    @SerializedName("on_mandatory_software_update_failure")
    ON_MANDATORY_SOFTWARE_UPDATE_FAILURE("on_mandatory_software_update_failure"),
    @SerializedName("on_mandatory_software_update_ready_for_application")
    ON_MANDATORY_SOFTWARE_UPDATE_READY_FOR_APPLICATION("on_mandatory_software_update_ready_for_application"),
    @SerializedName("reboot")
    REBOOT("reboot"),
    @SerializedName("factory_reset")
    FACTORY_RESET("factory_reset");

    private String systemAction;

    private SystemAction(String system_action) {
        this.systemAction = system_action;
    }

    public String getString() {
        return systemAction;
    }

    public static SystemAction value(String action) {
        for (SystemAction e : values()) {
            if (e.systemAction.equals(action)) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }

}
