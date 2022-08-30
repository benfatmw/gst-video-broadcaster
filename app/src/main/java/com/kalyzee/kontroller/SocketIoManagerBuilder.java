package com.kalyzee.kontroller;


import android.os.Build;

import androidx.annotation.RequiresApi;


import com.kalyzee.kontroller_services_api_implem.camera.CameraManagerImplem;
import com.kalyzee.kontroller_services_api_implem.network.NetworkSettingManagerImplem;
import com.kalyzee.kontroller_services_api_implem.system.SystemManagerImplem;
import com.kalyzee.kontroller_services_api_implem.system.update.UpdateSessionManagerImplem;
import com.kalyzee.kontroller_services_api_implem.system.update.download.DownloadSessionManagerImplem;
import com.kalyzee.kontroller_services_api_implem.system.update.install.UpdateInstallerImplem;
import com.kalyzee.kontroller_services_api_implem.video.VideoContextChangedListener;
import com.kalyzee.kontroller_services_api_implem.video.VideoManagerImplem;
import com.kalyzee.panel_connection_manager.SocketIoManager;
import com.kalyzee.panel_connection_manager.executors.camera.CameraRequestsExecutor;
import com.kalyzee.panel_connection_manager.executors.network.NetworkRequestsExecutor;
import com.kalyzee.panel_connection_manager.executors.system.SystemRequestsExecutor;
import com.kalyzee.panel_connection_manager.executors.video.VideoRequestsExecutor;
import com.kalyzee.panel_connection_manager.utils.SocketSSLBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import io.socket.client.Socket;
import okhttp3.OkHttpClient;

/**
 * Class allowing to build a socketIoManager object after instantiating and injecting its dependencies
 */
public class SocketIoManagerBuilder {

    private final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .build();
    private Socket socket;

    private CameraRequestsExecutor cameraRequestsExecutor;
    private NetworkRequestsExecutor networkRequestsExecutor;
    private VideoRequestsExecutor videoRequestsExecutor;
    private SystemRequestsExecutor systemRequestsExecutor;

    private VideoContextChangedListener videoContextChangedListener;

    private CameraManagerImplem cameraManager;
    private VideoManagerImplem videoManager;

    private UpdateSessionManagerImplem updateSessionManager;
    private DownloadSessionManagerImplem downloadSessionManager;
    private SystemManagerImplem systemManager;
    private NetworkSettingManagerImplem networkSettingsManager;
    private UpdateInstallerImplem updateInstaller;
    private String panelUri;

    public SocketIoManagerBuilder(String panelUri) {
        this.panelUri = panelUri;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public SocketIoManager build() throws GeneralSecurityException, IOException, URISyntaxException {

        socket = new SocketSSLBuilder().setURL(panelUri).build();

        /** Instantiate managers */
        cameraManager = new CameraManagerImplem();
        videoManager = new VideoManagerImplem();
        networkSettingsManager = new NetworkSettingManagerImplem();
        downloadSessionManager = new DownloadSessionManagerImplem();

        updateInstaller = new UpdateInstallerImplem();
        systemManager = new SystemManagerImplem();
        updateSessionManager = new UpdateSessionManagerImplem();

        /** Instantiate Events listeners */
        videoContextChangedListener = new VideoContextChangedListener();
        videoManager.registerContextChangedListener(videoContextChangedListener);

        /** Instantiate and register events listeners */
        cameraRequestsExecutor = new CameraRequestsExecutor(cameraManager);
        networkRequestsExecutor = new NetworkRequestsExecutor(networkSettingsManager);
        videoRequestsExecutor = new VideoRequestsExecutor(videoManager);
        systemRequestsExecutor = new SystemRequestsExecutor(systemManager, updateSessionManager);

        /** Create a SocketIoManager object */
        return new SocketIoManager(socket, cameraRequestsExecutor,
                networkRequestsExecutor, videoRequestsExecutor,
                systemRequestsExecutor);
    }
}
