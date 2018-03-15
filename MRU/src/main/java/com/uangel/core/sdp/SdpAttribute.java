package com.uangel.core.sdp;

public class SdpAttribute {

    public static final int PAYLOADID_NONE = -1;

    private int payloadId;
    private String description;

    public SdpAttribute() {
    }

    public SdpAttribute(int payloadId, String description) {
        this.payloadId = payloadId;
        this.description = description;
    }

    public int getPayloadId() {
        return payloadId;
    }

    public void setPayloadId(int payloadId) {
        this.payloadId = payloadId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
