package x3.player.mru.session.StateHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.session.SessionInfo;
import x3.player.mru.session.SessionState;

public class OfferStateFunction implements StateFunction {
    private static final Logger logger = LoggerFactory.getLogger(OfferStateFunction.class);

    @Override
    public void run(SessionInfo sessionInfo, Object arg) {
        if (sessionInfo == null) {
            return;
        }

        if (sessionInfo.getServiceState() != SessionState.OFFER) {
            sessionInfo.setServiceState(SessionState.OFFER);
        }

        //
        // TODO
        //
    }
}
