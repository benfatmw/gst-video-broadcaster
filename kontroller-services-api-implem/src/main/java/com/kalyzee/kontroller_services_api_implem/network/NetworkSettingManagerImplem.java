package com.kalyzee.kontroller_services_api_implem.network;

import com.kalyzee.kontroller_services_api.dtos.network.NetworkInformation;
import com.kalyzee.kontroller_services_api.exceptions.network.GetNetworkInformationException;
import com.kalyzee.kontroller_services_api.interfaces.network.NetworkSettingManager;

public class NetworkSettingManagerImplem implements NetworkSettingManager {
    @Override
    public NetworkInformation getInformation() throws GetNetworkInformationException {
        return null;
    }
}
