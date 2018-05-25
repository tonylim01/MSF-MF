package x3.player.mru.surfif.module;

public class SurfPlayInfo {

    private String sessionId;
    private int channel;
    private String filename;
    private boolean isPlaying;

    public SurfPlayInfo(String sessionId, int channel, String filename) {
        this.sessionId = sessionId;
        this.channel = channel;
        this.filename = filename;
        this.isPlaying = false;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
