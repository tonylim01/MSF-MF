package x3.player.mru.rmqif.messages;

import com.google.gson.annotations.SerializedName;

public class HeartbeatReq {
    @SerializedName("session_total")
    private int sessionTotal;
    @SerializedName("session_idle")
    private int sessionIdle;

    @SerializedName("conference_channel_total")
    private int conferenceChannelTotal;
    @SerializedName("conference_channel_idle")
    private int conferenceChannelIdle;

    public int getSessionTotal() {
        return sessionTotal;
    }

    public void setSessionTotal(int sessionTotal) {
        this.sessionTotal = sessionTotal;
    }

    public int getSessionIdle() {
        return sessionIdle;
    }

    public void setSessionIdle(int sessionIdle) {
        this.sessionIdle = sessionIdle;
    }

    public int getConferenceChannelTotal() {
        return conferenceChannelTotal;
    }

    public void setConferenceChannelTotal(int conferenceChannelTotal) {
        this.conferenceChannelTotal = conferenceChannelTotal;
    }

    public int getConferenceChannelIdle() {
        return conferenceChannelIdle;
    }

    public void setConferenceChannelIdle(int conferenceChannelIdle) {
        this.conferenceChannelIdle = conferenceChannelIdle;
    }
}
