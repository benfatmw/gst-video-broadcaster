package com.kalyzee.panel_connection_manager.mappers.system;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kalyzee.panel_connection_manager.exceptions.InvalidRequestActionException;

public enum SystemAction {

    @JsonProperty("get_information")
    GET_INFORMATION("get_information"),
    @JsonProperty("get_context")
    GET_CONTEXT("get_context"),
    @JsonProperty("on_context_updated")
    ON_CONTEXT_UPDATED("on_context_updated"),
    @JsonProperty("get_software_update_context")
    GET_SOFTWARE_UPDATE_CONTEXT("get_software_update_context"),
    @JsonProperty("start_mandatory_software_download")
    START_MANDATORY_SOFTWARE_DOWNLOAD("start_mandatory_software_download"),
    @JsonProperty("install_mandatory_software_update")
    INSTALL_MANDATORY_SOFTWARE_UPDATE("install_mandatory_software_update"),
    @JsonProperty("abort_mandatory_software_update")
    ABORT_MANDATORY_SOFTWARE_UPDATE("abort_mandatory_software_update"),
    @JsonProperty("on_mandatory_software_download_progress")
    ON_MANDATORY_SOFTWARE_DOWNLOAD_PROGRESS("on_mandatory_software_download_progress"),
    @JsonProperty("on_mandatory_software_update_failure")
    ON_MANDATORY_SOFTWARE_UPDATE_FAILURE("on_mandatory_software_update_failure"),
    @JsonProperty("on_mandatory_software_update_ready_for_application")
    ON_MANDATORY_SOFTWARE_UPDATE_READY_FOR_APPLICATION("on_mandatory_software_update_ready_for_application"),
    @JsonProperty("schedule_silent_software_update")
    SCHEDULE_SILENT_SOFTWARE_UPDATE("schedule_silent_software_update"),
    @JsonProperty("abort_silent_software_update")
    ABORT_SILENT_SOFTWARE_UPDATE("abort_silent_software_update"),
    @JsonProperty("reboot")
    REBOOT("reboot"),
    @JsonProperty("factory_reset")
    FACTORY_RESET("factory_reset"),
    @JsonProperty("set_time")
    SET_TIME("set_time"),
    @JsonProperty("set_time_zone")
    SET_TIME_ZONE("set_time_zone");

    private String systemAction;

    private SystemAction(String systemAction) {
        this.systemAction = systemAction;
    }

    public String getString() {
        return systemAction;
    }

    @JsonValue
    public static SystemAction value(String action) {
        for (SystemAction e : values()) {
            if (e.systemAction.equals(action)) {
                return e;
            }
        }
        throw new InvalidRequestActionException("Input action: " + action + " is not supported.");
    }

}
