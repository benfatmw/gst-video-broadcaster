package com.kalyzee.kontroller_services_api_implem.network;

import android.net.ConnectivityManager;
import android.net.Network;


import androidx.annotation.NonNull;

import com.kalyzee.kontroller_services_api.interfaces.network.INetworkCallback;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NetworkCallback extends ConnectivityManager.NetworkCallback {

    private static String TAG = "NetworkCallback";
    private static List<INetworkCallback> networkCallbacksList = new ArrayList<>();

    @Override
    public void onAvailable(@NonNull Network network) {
        super.onAvailable(network);
        /** Iterating networkCallbacksList ArrayList using Iterator */
        Iterator itr = networkCallbacksList.iterator();
        while (itr.hasNext()) {
            @SuppressWarnings("unchecked")
            INetworkCallback listener = (INetworkCallback) itr.next();
            listener.onChanged();
        }
    }

    @Override
    public void onLost(@NonNull Network network) {
        /** Iterating networkCallbacksList ArrayList using Iterator */
        Iterator itr = networkCallbacksList.iterator();
        while (itr.hasNext()) {
            @SuppressWarnings("unchecked")
            INetworkCallback listener = (INetworkCallback) itr.next();
            listener.onChanged();
        }
        super.onLost(network);
    }


    public static void unregisterListener(INetworkCallback listener) {
        networkCallbacksList.remove(listener);
    }

    public static void registerListener(INetworkCallback listener) {
        networkCallbacksList.add(listener);
    }


}
