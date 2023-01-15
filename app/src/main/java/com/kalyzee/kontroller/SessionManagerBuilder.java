package com.kalyzee.kontroller;


import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.Room;


import com.kalyzee.kontroller_services_api.interfaces.admin.AdminManager;
import com.kalyzee.kontroller_services_api.interfaces.system.update.IUpdateSessionManager;
import com.kalyzee.kontroller_services_api.interfaces.system.update.IUpdateStateListener;
import com.kalyzee.kontroller_services_api.interfaces.system.update.install.IUpdateInstaller;
import com.kalyzee.kontroller_services_api.interfaces.system.update.mandatory.IMandatoryUpdateManager;
import com.kalyzee.kontroller_services_api.interfaces.system.update.silent.IScheduledUpdateManager;
import com.kalyzee.kontroller_services_api_implem.admin.AdminManagerImplem;
import com.kalyzee.kontroller_services_api_implem.camera.CameraManagerImplem;
import com.kalyzee.kontroller_services_api_implem.network.NetworkSettingManagerImplem;
import com.kalyzee.kontroller_services_api_implem.system.SystemManagerImplem;
import com.kalyzee.kontroller_services_api_implem.system.update.UpdateSessionManager;
import com.kalyzee.kontroller_services_api_implem.system.update.dao.DownloadSessionDao;
import com.kalyzee.kontroller_services_api_implem.system.update.dao.UpdateSessionDao;
import com.kalyzee.kontroller_services_api_implem.system.update.db.AppDatabase;
import com.kalyzee.kontroller_services_api_implem.system.update.download.DownloadSessionManager;
import com.kalyzee.kontroller_services_api_implem.system.update.install.UpdateInstaller;
import com.kalyzee.kontroller_services_api_implem.system.update.mandatory.MandatoryUpdateManager;
import com.kalyzee.kontroller_services_api_implem.system.update.silent.ScheduledUpdateManager;
import com.kalyzee.kontroller_services_api_implem.system.update.silent.SilentUpdateStateListener;
import com.kalyzee.kontroller_services_api_implem.video.VideoManagerGstImplem;
import com.kalyzee.kontroller_services_api_implem.video.VideoManagerImplem;
import com.kalyzee.panel_connection_manager.CredentialsManager;
import com.kalyzee.panel_connection_manager.Session;
import com.kalyzee.panel_connection_manager.SessionManager;
import com.kalyzee.panel_connection_manager.executors.admin.AdminRequestsExecutor;
import com.kalyzee.panel_connection_manager.executors.camera.CameraRequestsExecutor;
import com.kalyzee.panel_connection_manager.executors.network.NetworkRequestsExecutor;
import com.kalyzee.panel_connection_manager.executors.system.SystemRequestsExecutor;
import com.kalyzee.panel_connection_manager.executors.video.VideoRequestsExecutor;
import com.kalyzee.panel_connection_manager.mappers.session.ILoginStatusListener;
import com.kalyzee.panel_connection_manager.utils.SocketSSLBuilder;
import com.kalyzee.visca_over_ip.ViscaCamera;
import com.kalyzee.visca_over_ip.ViscaSpecification;

import org.freedesktop.gstreamer.pipeline.CameraStreamPipeline;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.concurrent.TimeUnit;

import io.socket.client.Socket;
import okhttp3.OkHttpClient;

/**
 * Class allowing to build a socketIoManager object after instantiating and injecting its dependencies
 */
public class SessionManagerBuilder {

    private final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .connectTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
    private Socket socket;

    private CameraRequestsExecutor cameraRequestsExecutor;
    private NetworkRequestsExecutor networkRequestsExecutor;
    private VideoRequestsExecutor videoRequestsExecutor;
    private SystemRequestsExecutor systemRequestsExecutor;
    private AdminRequestsExecutor adminRequestsExecutor;

    private ViscaCamera viscaCamera;
    private CameraManagerImplem cameraManager;
    //private VideoManagerImplem videoManager;
    private VideoManagerGstImplem videoManager;
    private CameraStreamPipeline cameraStreamPipeline;
    private SystemManagerImplem systemManager;
    private NetworkSettingManagerImplem networkSettingsManager;
    private AdminManager adminManager;

    private IUpdateSessionManager updateSessionManager;
    private IScheduledUpdateManager scheduledUpdateManager;
    private IMandatoryUpdateManager mandatoryUpdateManager;
    private DownloadSessionManager downloadSessionManager;
    private UpdateSessionDao updateSessionDao;
    private DownloadSessionDao downloadSessionDao;
    private IUpdateInstaller updateInstaller;
    private IUpdateStateListener silentUpdateStateListener;

    private Session session;

    private final String panelUri;
    private final CredentialsManager credentialsManager;
    private final Context context;
    private final String ip;
    private final int viscaPort;
    private final String rtspLocation;
    private final ILoginStatusListener loginStatusListener;

    public SessionManagerBuilder(CredentialsManager credentialsManager, ILoginStatusListener loginStatusListener,
                                 Context context,  String ip, int viscaPort, String rtspLocation) {
        this.credentialsManager = credentialsManager;
        this.panelUri = credentialsManager.getPanelUri();
        this.context = context;
        this.ip = ip;
        this.viscaPort = viscaPort;
        this.rtspLocation = rtspLocation;
        this.loginStatusListener = loginStatusListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public SessionManager build() throws GeneralSecurityException, IOException, URISyntaxException {

        socket = new SocketSSLBuilder().setURL(panelUri).build();

        AppDatabase appDatabase = Room.databaseBuilder(context,
                AppDatabase.class, "app-database").build();

        /** Instantiate managers */
        viscaCamera = new ViscaCamera(ip, viscaPort);
        cameraManager = new CameraManagerImplem(viscaCamera);
        //videoManager = new VideoManagerImplem(context);
        cameraStreamPipeline = new CameraStreamPipeline(context, rtspLocation);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        videoManager = new VideoManagerGstImplem(context, cameraStreamPipeline);
        networkSettingsManager = new NetworkSettingManagerImplem(context);
        systemManager = new SystemManagerImplem();
        adminManager = new AdminManagerImplem(credentialsManager);

        /** Software update dependencies */
        downloadSessionDao = appDatabase.DownloadSessionDao();
        downloadSessionManager = new DownloadSessionManager(okHttpClient,
                downloadSessionDao);
        updateInstaller = new UpdateInstaller(context);
        updateSessionDao = appDatabase.UpdateSessionDao();
        updateSessionManager = new UpdateSessionManager(
                downloadSessionManager, systemManager, socket,
                updateSessionDao, updateInstaller);
        silentUpdateStateListener = new SilentUpdateStateListener(
                updateSessionManager, updateSessionDao);
        scheduledUpdateManager = new ScheduledUpdateManager(updateSessionManager,
                silentUpdateStateListener,
               context);
        mandatoryUpdateManager = new MandatoryUpdateManager(updateSessionManager);


        /** Instantiate and register events listeners */
        cameraRequestsExecutor = new CameraRequestsExecutor(cameraManager);
        networkRequestsExecutor = new NetworkRequestsExecutor(networkSettingsManager);
        videoRequestsExecutor = new VideoRequestsExecutor(videoManager);
        adminRequestsExecutor = new AdminRequestsExecutor(adminManager);
        systemRequestsExecutor = new SystemRequestsExecutor(systemManager,
                updateSessionManager,
                scheduledUpdateManager,
                mandatoryUpdateManager);

        /** Create a SocketIoManager object */
        session = new Session(socket, cameraRequestsExecutor,
                networkRequestsExecutor, videoRequestsExecutor,
                systemRequestsExecutor,
                adminRequestsExecutor);
        session.registerLoginStatusListener(loginStatusListener);
        /** Create a SessionManager object */
        return new SessionManager(session, credentialsManager);
    }
}
