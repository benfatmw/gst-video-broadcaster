package com.kalyzee.kontroller_services_api.interfaces.system.update;


import com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionModel;
import com.kalyzee.kontroller_services_api.dtos.system.update.download.DownloadSessionModel;

import java.util.List;

public interface IUpdateSessionManager {

    /**
     * Creates a new update session. An update session is used to make modifications to a system
     * binary (OS, application ...).
     *
     * @param updateSession   Specification for the new update session to be created.
     * @param downloadSession Specification for the related download session to be created.
     * @return Identifier of the new update session being created.
     */
    String create(UpdateSessionModel updateSession,
                  DownloadSessionModel downloadSession);

    /**
     * Starts a new update session. An update session is used to make modifications to a system binary
     * (OS, application ...).
     *
     * @param sessionId           Identifier of the update session.
     * @param updateStateListener Receives the session state of the operation invocation.
     */
    void start(String sessionId,
               IUpdateStateListener updateStateListener);

    /**
     * Starts a new update session. An update session is used to make modifications to a system binary
     * (OS, appllication ...).
     *
     * @param sessionId Identifier of the update session.
     */
    void start(String sessionId);

    /**
     * Completes the update session with the specified identifier. This call triggers binary
     * installation if download has finished
     *
     * @param sessionId Identifier of the update session.
     */
    void complete(String sessionId);

    /**
     * Cancels an update session with the specified identifier.
     *
     * @param sessionId Identifier of the update session.
     */
    void cancel(String sessionId);

    /**
     * Destroys an update session with the specified identifier.
     *
     * @param sessionId Identifier of the update session.
     */
    void destroy(String sessionId);

    /**
     * Gets the update session with the specified identifier, including the most up-to-date status
     * information for the session.
     *
     * @param sessionId Identifier of the update session.
     * @return update session attributes.
     */
    UpdateSessionModel getById(String sessionId);

    /**
     * Retrieves the list of all available update sessions contexts
     *
     * @return List of all available update sessions contexts.
     */
    List<UpdateSessionModel> getAll();

    /**
     * Starts a new update session. An update session is used to make modifications to a system binary
     * (OS, application ...).
     *
     * @param sessionId           Identifier of the update session.
     * @param updateStateListener Receives the session state of the operation invocation.
     */
    void resume(String sessionId, IUpdateStateListener updateStateListener);
}
