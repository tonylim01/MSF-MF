package x3.player.mru.session.StateHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.session.SessionInfo;
import x3.player.mru.session.SessionState;

public class ReadyStateFunction implements StateFunction {
    private static final Logger logger = LoggerFactory.getLogger(ReadyStateFunction.class);

    @Override
    public void run(SessionInfo sessionInfo) {
        if (sessionInfo == null) {
            return;
        }

        if (sessionInfo.getServiceState() != SessionState.READY) {
            sessionInfo.setServiceState(SessionState.READY);
        }

        //
        // TODO
        //

    }
}
