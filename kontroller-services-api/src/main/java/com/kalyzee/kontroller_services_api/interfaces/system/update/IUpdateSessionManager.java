package com.kalyzee.kontroller_services_api.interfaces.system.update;


import com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionModel;
import com.kalyzee.kontroller_services_api.dtos.system.update.download.DownloadSessionModel;

public interface IUpdateSessionManager {

    /**
     * Creates a new update session. An update session is used to make modifications to a system binary (OS, appllication ...).
     *
     * @param updateSessionCreateSpec   Specification for the new update session to be created.
     * @param downloadSessionCreateSpec Specification for the related download session to be created.
     * @return Identifier of the new update session being created.
     */
    String create(UpdateSessionModel updateSessionCreateSpec, DownloadSessionModel downloadSessionCreateSpec);

    /**
     * Creates a new update session. An update session is used to make modifications to a system binary (OS, appllication ...).
     *
     * @param updateSessionCreateSpec Specification for the new update session to be created.
     * @param downloadSessionCreateSpec Specification for the related download session to be created.
     * @param updateStateChangeHandler Receives the session state of the operation invocation.
     * @return Identifier of the new update session being created.
     */
    String create(UpdateSessionModel updateSessionCreateSpec, DownloadSessionModel downloadSessionCreateSpec, IUpdateStateChangeHandler updateStateChangeHandler);

    /**
     * Completes the update session with the specified identifier. This call triggers binary
     * installation if download has finished
     *
     * @param sessionId Identifier of the update session.
     */
    void complete(String sessionId);

    /**
     * Deletes an update session with the specified identifier.
     *
     * @param sessionId Identifier of the update session.
     */
    void delete(String sessionId);

    /**
     * Gets the update session with the specified identifier, including the most up-to-date status
     * information for the session.
     *
     * @param sessionId Identifier of the update session.
     * @return update session attributes.
     */
    UpdateSessionModel get(String sessionId);
}
