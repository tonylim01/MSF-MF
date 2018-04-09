package x3.player.mru.session;

import x3.player.core.sdp.SdpInfo;
import x3.player.mru.rmqif.messages.FileData;

public class SessionInfo {

    private String sessionId;
    private long createdTime;

    private SessionState serviceState;
    private long lastSentTime;
    private long t2Time;
    private long t4Time;

    private String conferenceId;
    private SdpInfo sdpInfo;

    private String localIpAddress;
    private int srcLocalPort;
    private int dstLocalPort;

    private boolean isCaller;
    private String fromNo;
    private String toNo;
    private String aiifName;

    private FileData fileData;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public SessionState getServiceState() {
        return serviceState;
    }

    public void setServiceState(SessionState serviceState) {
        synchronized (this) {
            this.serviceState = serviceState;
            this.lastSentTime = 0;
            this.t2Time = 0;
            this.t4Time = 0;
        }
    }

    public long getLastSentTime() {
        synchronized (this) {
            return lastSentTime;
        }
    }

    public void setLastSentTime(long lastSentTime) {
        synchronized (this) {
            this.lastSentTime = lastSentTime;
        }
    }

    public void setLastSentTime() {
        setLastSentTime(System.currentTimeMillis());
    }

    public long getT2Time() {
        synchronized (this) {
            return t2Time;
        }
    }

    public void setT2Time(long t2Time) {
        synchronized (this) {
            this.t2Time = t2Time;
        }
    }

    public long getT4Time() {
        synchronized (this) {
            return t4Time;
        }
    }

    public void setT4Time(long t4Time) {
        this.t4Time = t4Time;
    }

    public void updateT2Time(long t2interval) {
        synchronized (this) {
            this.t2Time = System.currentTimeMillis() + t2interval;
        }
    }

    public void updateT4Time(long t4interval) {
        synchronized (this) {
            this.t4Time = System.currentTimeMillis() + t4interval;
        }
    }

    public String getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(String conferenceId) {
        this.conferenceId = conferenceId;
    }

    public SdpInfo getSdpInfo() {
        return sdpInfo;
    }

    public void setSdpInfo(SdpInfo sdpInfo) {
        this.sdpInfo = sdpInfo;
    }

    public String getLocalIpAddress() {
        return localIpAddress;
    }

    public void setLocalIpAddress(String localIpAddress) {
        this.localIpAddress = localIpAddress;
    }

    public String getFromNo() {
        return fromNo;
    }

    public void setFromNo(String fromNo) {
        this.fromNo = fromNo;
    }

    public String getToNo() {
        return toNo;
    }

    public void setToNo(String toNo) {
        this.toNo = toNo;
    }

    public boolean isCaller() {
        return isCaller;
    }

    public void setCaller(boolean caller) {
        isCaller = caller;
    }

    public int getSrcLocalPort() {
        return srcLocalPort;
    }

    public void setSrcLocalPort(int srcLocalPort) {
        this.srcLocalPort = srcLocalPort;
    }

    public int getDstLocalPort() {
        return dstLocalPort;
    }

    public void setDstLocalPort(int dstLocalPort) {
        this.dstLocalPort = dstLocalPort;
    }

    public String getAiifName() {
        return aiifName;
    }

    public void setAiifName(String aiifName) {
        this.aiifName = aiifName;
    }

    public FileData getFileData() {
        return fileData;
    }

    public void setFileData(FileData fileData) {
        this.fileData = fileData;
    }
}
