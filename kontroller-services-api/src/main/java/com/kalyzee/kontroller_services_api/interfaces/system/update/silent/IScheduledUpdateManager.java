package com.kalyzee.kontroller_services_api.interfaces.system.update.silent;

import com.kalyzee.kontroller_services_api.dtos.system.update.silent.SilentUpdateDescriptor;
import com.kalyzee.kontroller_services_api.exceptions.system.update.silent.CancelScheduledUpdateException;
import com.kalyzee.kontroller_services_api.exceptions.system.update.silent.ScheduleSilentUpdateFailureException;

public interface IScheduledUpdateManager {
    /**
     * Schedules a new silent update session.
     * An update session is used to make modifications to a system binary (OS, appllication ...).
     *
     * @param updateDescriptor   Specification for the new silent update session to be scheduled.
     * @throws ScheduleSilentUpdateFailureException
     * @return Identifier of the new silent update session being scheduled.
     */
    String schedule(SilentUpdateDescriptor updateDescriptor) throws ScheduleSilentUpdateFailureException;
    /**
     * Cancels/aborts a scheduled update session.
     * An update session is used to make modifications to a system binary (OS, appllication ...).
     * @throws CancelScheduledUpdateException
     * @param sessionId Identifier of the new update session being created.
     */
    void cancel(String sessionId) throws CancelScheduledUpdateException;

}
