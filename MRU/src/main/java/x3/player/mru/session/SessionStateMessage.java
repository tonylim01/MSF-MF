package x3.player.mru.session;

public class SessionStateMessage {

    private String sessionId;
    private SessionState state;

    public SessionStateMessage() {
    }

    public SessionStateMessage(String sessionId, SessionState state) {
        this.sessionId = sessionId;
        this.state = state;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public SessionState getState() {
        return state;
    }

    public void setState(SessionState state) {
        this.state = state;
    }
}
