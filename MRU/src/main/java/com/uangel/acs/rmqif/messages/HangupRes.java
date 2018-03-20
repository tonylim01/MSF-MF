package com.uangel.acs.rmqif.messages;

import com.google.gson.annotations.SerializedName;

public class HangupRes {

    @SerializedName("conference_id")
    private String conferenceId;
    @SerializedName("participant_count")
    private int participantCount;

    public String getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(String conferenceId) {
        this.conferenceId = conferenceId;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }
}
