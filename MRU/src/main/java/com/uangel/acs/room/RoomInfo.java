package com.uangel.acs.room;

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
}
