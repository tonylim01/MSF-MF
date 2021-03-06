package x3.player.mru.rmqif.types;

public class RmqHeader {

    private String type;
    private String callId;
    private String transactionId;
    private String msgFrom;
    private int trxType;
    private int reasonCode;
    private String reason;

    public RmqHeader() {

    }

    public RmqHeader(String type, String callId, String transactionId, String msgFrom, int trxType, int reasonCode, String reason) {
        this.type = type;
        this.callId = callId;
        this.transactionId = transactionId;
        this.msgFrom = msgFrom;
        this.trxType = trxType;
        this.reasonCode = reasonCode;
        this.reason = reason;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSessionId() {
        return callId;
    }

    public void setSessionId(String sessionId) {
        this.callId = sessionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getMsgFrom() {
        return msgFrom;
    }

    public void setMsgFrom(String msgFrom) {
        this.msgFrom = msgFrom;
    }

    public int getTrxType() {
        return trxType;
    }

    public void setTrxType(int trxType) {
        this.trxType = trxType;
    }

    public int getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(int reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "RmqHeader{" +
                "type='" + type + '\'' +
                ", sessionId='" + callId + '\'' +
                ", transactionId=" + transactionId +
                ", msgFrom='" + msgFrom + '\'' +
                ", trxType=" + trxType +
                ", reasonCode=" + reasonCode +
                ", reason='" + reason + '\'' +
                '}';
    }
}
