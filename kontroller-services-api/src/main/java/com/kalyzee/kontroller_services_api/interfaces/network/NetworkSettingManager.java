package com.kalyzee.kontroller_services_api.interfaces.network;

import com.kalyzee.kontroller_services_api.dtos.network.NetworkInformation;
import com.kalyzee.kontroller_services_api.exceptions.network.GetNetworkInformationException;

public interface NetworkSettingManager {
    NetworkInformation getInformation() throws GetNetworkInformationException;
}
