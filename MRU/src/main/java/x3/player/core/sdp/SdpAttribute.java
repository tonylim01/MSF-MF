package x3.player.core.sdp;

public class SdpAttribute {

    public static final int PAYLOADID_NONE = -1;

    public static final String NAME_RTPMAP = "rtpmap";

    private String name;
    private int payloadId;
    private String description;

    public SdpAttribute() {
    }

    public SdpAttribute(int payloadId, String description) {
        this.payloadId = payloadId;
        this.description = description;

        if (payloadId != PAYLOADID_NONE) {
            name = NAME_RTPMAP;
        }
    }

    public SdpAttribute(String name, String description) {
        this.name = name;
        this.payloadId = PAYLOADID_NONE;
        this.description = description;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
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
