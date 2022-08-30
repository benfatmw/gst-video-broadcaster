package com.kalyzee.panel_connection_manager.utils;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.transports.WebSocket;
import okhttp3.OkHttpClient;

public class SocketSSLBuilder {

    private static final String TAG = "SocketSSLBuilder";
    private String socketURI;

    public SocketSSLBuilder setURL(String socketURL) {
        this.socketURI = socketURL;
        return this;
    }

    public Socket build() throws URISyntaxException, GeneralSecurityException, FileNotFoundException {
        Socket socket = null;
        /**
         * Set HTTPS context and OkHttpClient to IO options
         * to enable the Socket.IO to communicate with HTTPS servers
         */
        OkHttpClient okHttpClient;
        okHttpClient = HttpsUtils.getOkHttpClient();
        IO.setDefaultOkHttpWebSocketFactory(okHttpClient);
        IO.setDefaultOkHttpCallFactory(okHttpClient);
        IO.Options opts = new IO.Options();
        opts.forceNew = true;
        opts.reconnection = false;
        opts.secure = true;
        opts.callFactory = okHttpClient;
        opts.webSocketFactory = okHttpClient;
        opts.transports = new String[]{WebSocket.NAME};
        socket = IO.socket(socketURI, opts);
        return socket;
    }
}