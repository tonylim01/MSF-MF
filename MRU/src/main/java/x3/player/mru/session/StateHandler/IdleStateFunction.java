package x3.player.mru.session.StateHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.service.ServiceManager;
import x3.player.mru.session.SessionInfo;
import x3.player.mru.session.SessionState;

public class IdleStateFunction implements StateFunction {
    private static final Logger logger = LoggerFactory.getLogger(IdleStateFunction.class);

    @Override
    public void run(SessionInfo sessionInfo) {
        if (sessionInfo == null) {
            return;
        }

        logger.debug("[{}] IdleStateFunction", sessionInfo.getSessionId());

        if (sessionInfo.getServiceState() != SessionState.IDLE) {
            sessionInfo.setServiceState(SessionState.IDLE);
        }

        ServiceManager.getInstance().releaseResource(sessionInfo.getSessionId());
    }
}
