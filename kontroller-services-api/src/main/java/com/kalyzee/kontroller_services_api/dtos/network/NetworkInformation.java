package com.kalyzee.kontroller_services_api.dtos.network;


import com.fasterxml.jackson.annotation.JsonProperty;

public class NetworkInformation {
    @JsonProperty("ip")
    private String ip;
    @JsonProperty("mac")
    private String mac;
    @JsonProperty("netmask")
    private String netmask;
    @JsonProperty("gateway")
    private String gateway;
    @JsonProperty("dns")
    private String dns;

    public NetworkInformation(@JsonProperty("ip") String ip,
                              @JsonProperty("mac") String mac,
                              @JsonProperty("netmask") String netmask,
                              @JsonProperty("gateway") String gateway,
                              @JsonProperty("dns") String dns) {
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
