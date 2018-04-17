package x3.player.mru.session.StateHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.session.SessionInfo;
import x3.player.mru.session.SessionState;

public class StartStateFunction implements StateFunction {
    private static final Logger logger = LoggerFactory.getLogger(StartStateFunction.class);

    @Override
    public void run(SessionInfo sessionInfo, Object arg) {
        if (sessionInfo == null) {
            return;
        }

        if (sessionInfo.getServiceState() != SessionState.START) {
            sessionInfo.setServiceState(SessionState.START);
        }

        //
        // TODO
        //
    }
}
