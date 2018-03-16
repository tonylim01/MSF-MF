package com.uangel.acs.session;

import com.uangel.acs.AppInstance;
import com.uangel.acs.config.AmfConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SessionManager {

    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);

    private static final int DEFAULT_SESSION_MAX_SIZE = 2000;
    private static final int DEFAULT_SESSION_TIMEOUT = 3600;

    private static SessionManager sessionManager = null;

    public static SessionManager getSessionManager() {
        if (sessionManager == null) {
            sessionManager = new SessionManager();
        }

        return sessionManager;
    }

    private int sessionSize;
    private int sessionTimeout;

    private Map<String, SessionInfo> sessionInfos;

    private ScheduledExecutorService scheduleService;
    static ScheduledFuture<?> scheduleFuture;
    private SessionMonitorRunnable sessionMonitorRunnable;

    public SessionManager() {

        AmfConfig config = AppInstance.getInstance().getConfig();
        if (config == null) {
            return;
        }

        sessionSize = config.getSessionMaxSize();
        sessionTimeout = config.getSessionTimeout();

        if (sessionSize <= 0 || sessionSize > DEFAULT_SESSION_MAX_SIZE) {
            sessionSize = DEFAULT_SESSION_MAX_SIZE;
        }

        if (sessionTimeout < 0) {
            sessionTimeout = DEFAULT_SESSION_TIMEOUT;
        }

        sessionInfos = new HashMap<>();

        scheduleService = Executors.newScheduledThreadPool(1);
        sessionMonitorRunnable = new SessionMonitorRunnable();
    }

    public void start() {
        scheduleFuture = scheduleService.scheduleAtFixedRate(sessionMonitorRunnable, 1, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        scheduleFuture.cancel(false);
    }

    public SessionInfo createSession(String sessionId) {

        if (sessionId == null) {
            logger.error("newSessionInfo() failed: Null sessionId");
            return null;
        }

        if (sessionInfos.containsKey(sessionId)) {
            logger.warn("newSessionInfo() failed: Already sessionId [{}] found", sessionId);
            return sessionInfos.get(sessionId);
        }

        SessionInfo sessionInfo = new SessionInfo();

        sessionInfo.setSessionId(sessionId);
        sessionInfo.setTimestamp(System.currentTimeMillis());
        //
        // TODO
        //

        synchronized (sessionInfos) {
            sessionInfos.put(sessionId, sessionInfo);
        }

        logger.debug("[{}] New session. Total [{}]", sessionId, sessionInfos.size());

        return sessionInfo;
    }

    public void deleteSession(String sessionId) {
        synchronized (sessionInfos) {
            sessionInfos.remove(sessionId);
        }

        logger.debug("[{}] Delete session", sessionId);
    }

    public void checkSessionValidity() {
        long current = System.currentTimeMillis();

        synchronized (sessionInfos) {
            logger.debug("Session count: {}", sessionInfos.size());

            for (Map.Entry<String, SessionInfo> entry: sessionInfos.entrySet()) {
                SessionInfo sessionInfo = entry.getValue();
                if (sessionInfo == null) {
                    continue;
                }

                if (current - sessionInfo.getTimestamp() >= sessionTimeout) {
                    //
                    // TODO: Session timeout expired
                    //
                }
            }
        }

        long elapsed = System.currentTimeMillis() - current;
        logger.debug("Sleep diff [{}]", elapsed);
    }

    class SessionMonitorRunnable implements Runnable {
        @Override
        public void run() {
            SessionManager manager = SessionManager.getSessionManager();

            if (manager != null) {
                manager.checkSessionValidity();
            }
        }
    }
}
