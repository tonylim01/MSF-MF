package x3.player.mru.session.StateHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.session.SessionInfo;
import x3.player.mru.session.SessionManager;
import x3.player.mru.session.SessionState;

public class ReleaseStateFunction implements StateFunction {
    private static final Logger logger = LoggerFactory.getLogger(ReleaseStateFunction.class);

    @Override
    public void run(SessionInfo sessionInfo) {
        if (sessionInfo == null) {
            return;
        }

        logger.debug("[{}] ReleaseStateFunction", sessionInfo.getSessionId());

        if (sessionInfo.getServiceState() != SessionState.RELEASE) {
            sessionInfo.setServiceState(SessionState.RELEASE);
            sessionInfo.updateT4Time(SessionManager.TIMER_HANGUP_T4);
        }

        sessionInfo.setLastSentTime();
        sessionInfo.updateT2Time(SessionManager.TIMER_HANGUP_T2);
    }
}
