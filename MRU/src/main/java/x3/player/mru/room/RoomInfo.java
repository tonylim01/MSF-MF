package x3.player.mru.room;

import java.util.Vector;

public class RoomInfo {
    private Vector<String> sessions;

    public RoomInfo() {
        this.sessions = new Vector<>();
    }

    public boolean addSession(String sessionId) {
        if (sessions.contains(sessionId)) {
            return false;
        }

        sessions.add(sessionId);
        return true;
    }

    public void removeSession(String sessionId) {
        if (sessions.contains(sessionId)) {
            sessions.remove(sessionId);
        }
    }

    public boolean hasSession(String sessionId) {
        return sessions.contains(sessionId);
    }

    public int getSessionSize() {
        return sessions.size();
    }

    /**
     * Gets the other session for the sessionId
     * @param sessionId
     * @return
     */
    public String getOtherSession(String sessionId) {
        String otherSession = null;
        if (sessions.contains(sessionId) && sessions.size() > 1) {
            for (String session: sessions) {
                if (session != sessionId) {
                    otherSession = session;
                    break;
                }
            }
        }
        return otherSession;
    }
}
