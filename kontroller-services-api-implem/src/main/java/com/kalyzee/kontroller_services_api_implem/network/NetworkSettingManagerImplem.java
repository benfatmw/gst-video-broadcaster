package com.kalyzee.kontroller_services_api_implem.network;

import static android.content.Context.WIFI_SERVICE;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import com.kalyzee.kontroller_services_api.dtos.network.NetworkInformation;
import com.kalyzee.kontroller_services_api.exceptions.network.GetNetworkInformationException;
import com.kalyzee.kontroller_services_api.interfaces.network.NetworkSettingManager;

public class NetworkSettingManagerImplem implements NetworkSettingManager {


    private static final String TAG = "NetworkSettings";
    private static final String GET_NETWORK_INFORMATION = "Get network information.";

    private final Context context;

    public NetworkSettingManagerImplem(Context context) {
        this.context = context;
    }

    @Override
    public NetworkInformation getInformation() throws GetNetworkInformationException {
        Log.i(TAG, GET_NETWORK_INFORMATION);
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);;
        return new NetworkInformation( Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()),
                wifiManager.getConnectionInfo().getMacAddress(),
                Formatter.formatIpAddress(wifiManager.getDhcpInfo().netmask),
                Formatter.formatIpAddress(wifiManager.getDhcpInfo().gateway),
                Formatter.formatIpAddress(wifiManager.getDhcpInfo().dns1) );
    }

}
