package com.uangel.acs.rmqif.messages;

public class InboundSetOfferReq {

    private String from_no;
    private String to_no;
    private String conference_id;
    private String sdp;

    public String getFromNo() {
        return from_no;
    }

    public void setFromNo(String from_no) {
        this.from_no = from_no;
    }

    public String getToNo() {
        return to_no;
    }

    public void setToNo(String to_no) {
        this.to_no = to_no;
    }

    public String getConferenceId() {
        return conference_id;
    }

    public void setConferenceId(String conference_id) {
        this.conference_id = conference_id;
    }

    public String getSdp() {
        return sdp;
    }

    public void setSdp(String sdp) {
        this.sdp = sdp;
    }

    @Override
    public String toString() {
        return "InboundSetOfferReq{" +
                "from_no='" + from_no + '\'' +
                ", to_no='" + to_no + '\'' +
                ", conference_id='" + conference_id + '\'' +
                ", sdp='" + sdp + '\'' +
                '}';
    }
}
