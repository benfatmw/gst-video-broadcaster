package com.kalyzee.kontroller_services_api.interfaces.system.update.mandatory;

import com.kalyzee.kontroller_services_api.dtos.system.update.UpdateDescriptor;
import com.kalyzee.kontroller_services_api.exceptions.system.update.mandatory.CancelMandatoryUpdateFailureException;
import com.kalyzee.kontroller_services_api.exceptions.system.update.mandatory.CompleteMandatoryUpdateFailureException;
import com.kalyzee.kontroller_services_api.exceptions.system.update.mandatory.StartMandatoryUpdateFailureException;

public interface IMandatoryUpdateManager {

    /**
     * starts a new mandatory update download.
     * An update session is used to make modifications to a system binary (OS, appllication ...).
     *
     * @param updateDescriptor Specification for the new silent update session to be scheduled.
     * @throws StartMandatoryUpdateFailureException
     * @return Identifier of the new silent update session being scheduled.
     */
    String start(UpdateDescriptor updateDescriptor) throws StartMandatoryUpdateFailureException;

    /**
     * Cancels/aborts mandatory update session.
     * An update session is used to make modifications to a system binary (OS, appllication ...).
     *
     * @param sessionId Identifier of the new update session being created.
     * @throws CancelMandatoryUpdateFailureException
     */
    void cancel(String sessionId) throws CancelMandatoryUpdateFailureException;

    /**
     * Installs a mandatory update.
     * An update session is used to make modifications to a system binary (OS, appllication ...).
     *
     * @param sessionId Identifier of the new update session being created.
     * @throws CompleteMandatoryUpdateFailureException
     */
    void complete(String sessionId) throws CompleteMandatoryUpdateFailureException;
}
