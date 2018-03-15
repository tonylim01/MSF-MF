package com.uangel.core.sdp;

import java.util.ArrayList;
import java.util.List;

public class SdpInfo {

    private String remoteIp;
    private String remotePort;
    private int payloadId;

    List<SdpAttribute> attributes = null;

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public String getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(String remotePort) {
        this.remotePort = remotePort;
    }

    public int getPayloadId() {
        return payloadId;
    }

    public void setPayloadId(int payloadId) {
        this.payloadId = payloadId;
    }

    public List<SdpAttribute> getAttributes() {
        return attributes;
    }

    public void addAttribute(int payloadId, String description) {
        if (attributes == null) {
            attributes = new ArrayList<>();
        }
        attributes.add(new SdpAttribute(payloadId, description));
    }
}
