package com.kalyzee.kontroller_services_api.dtos.network;

import com.google.gson.annotations.SerializedName;

public class NetworkInformation {
    @SerializedName("ip")
    private String ip;
    @SerializedName("mac")
    private String mac;
    @SerializedName("netmask")
    private String netmask;
    @SerializedName("gateway")
    private String gateway;
    @SerializedName("dns")
    private String dns;

    public NetworkInformation(String ip, String mac, String netmask, String gateway, String dns) {
        this.ip = ip;
        this.mac = mac;
        this.netmask = netmask;
        this.gateway = gateway;
        this.dns = dns;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getDns() {
        return dns;
    }

    public void setDns(String dns) {
        this.dns = dns;
    }
}