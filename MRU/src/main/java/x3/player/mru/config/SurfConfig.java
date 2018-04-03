package x3.player.mru.config;

public class SurfConfig {

    private int majorVersion;
    private int minorVersion;
    private int keepAliveTime;

    private String surfIp;
    private int surfPort;

    public int getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    public String getSurfIp() {
        return surfIp;
    }

    public void setSurfIp(String surfIp) {
        this.surfIp = surfIp;
    }

    public int getSurfPort() {
        return surfPort;
    }

    public void setSurfPort(int surfPort) {
        this.surfPort = surfPort;
    }

    public int getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(int keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }
}
