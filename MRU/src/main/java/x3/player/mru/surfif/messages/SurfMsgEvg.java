package x3.player.mru.surfif.messages;

import com.google.gson.annotations.SerializedName;

public class SurfMsgEvg {
    private boolean enabled;
    @SerializedName("host_override")
    private Boolean hostOverride;
    @SerializedName("delay_or_delete")
    private String delayOrDelete;
    @SerializedName("convert_RTP_events_to_inband")
    private String convertRtpEventsToInband;
    @SerializedName("convert_inband_events_to_RTP")
    private Boolean convertInbandEventsToRtp;
    @SerializedName("relay_RTP_events")
    private Boolean relayRtpEvent;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean getHostOverride() {
        return hostOverride;
    }

    public void setHostOverride(boolean hostOverride) {
        this.hostOverride = hostOverride;
    }

    public String getDelayOrDelete() {
        return delayOrDelete;
    }

    public void setDelayOrDelete(String delayOrDelete) {
        this.delayOrDelete = delayOrDelete;
    }

    public String getConvertRtpEventsToInband() {
        return convertRtpEventsToInband;
    }

    public void setConvertRtpEventsToInband(String convertRtpEventsToInband) {
        this.convertRtpEventsToInband = convertRtpEventsToInband;
    }

    public boolean getConvertInbandEventsToRtp() {
        return convertInbandEventsToRtp;
    }

    public void setConvertInbandEventsToRtp(boolean convertInbandEventsToRtp) {
        this.convertInbandEventsToRtp = convertInbandEventsToRtp;
    }

    public boolean getRelayRtpEvent() {
        return relayRtpEvent;
    }

    public void setRelayRtpEvent(boolean relayRtpEvent) {
        this.relayRtpEvent = relayRtpEvent;
    }
}
