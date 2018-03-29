package x3.player.mru.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SessionStateManager {

    private static final Logger logger = LoggerFactory.getLogger(SessionStateManager.class);

    private static final int QUEUE_SIZE = 64;

    private static SessionStateManager sessionStateManager = null;

    public static SessionStateManager getInstance() {
        if (sessionStateManager == null) {
            sessionStateManager = new SessionStateManager();
        }
        return sessionStateManager;
    }

    private BlockingQueue<SessionStateMessage> stateQueue;
    private Thread stateMachineThread;

    public SessionStateManager() {
        stateQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);
        stateMachineThread = new Thread(new SessionStateMachine(stateQueue));
        stateMachineThread.start();

        logger.info("SessionStateManager started");
    }

    public void stop() {
        stateMachineThread.interrupt();
        stateMachineThread = null;
    }

    public void setState(String sessionId, SessionState state) {
        if (sessionId == null) {
            return;
        }

        SessionStateMessage msg = new SessionStateMessage(sessionId, state);
        stateQueue.add(msg);
    }

    class SessionStateMachine implements Runnable {

        private BlockingQueue<SessionStateMessage> queue;
        private boolean isQuit = false;

        public SessionStateMachine(BlockingQueue<SessionStateMessage> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            logger.info("SessionStateMachine started");

            while(!isQuit) {
                try {
                    SessionStateMessage msg = queue.take();
                    handleMessage(msg);
                } catch (Exception e) {
                    logger.warn("Exception [{}] [{}]", e.getClass(), e.getMessage());
                    if (e.getClass() == InterruptedException.class || e.getClass() == SocketException.class) {
                        isQuit = true;
                    }
                }
            }

            logger.warn("SessionStateMachine end");
        }

        private void handleMessage(SessionStateMessage msg) {
            if (msg == null) {
                return;
            }

            logger.debug("[{}] State message [{}]", msg.getSessionId(), msg.getState().name());

            SessionInfo sessionInfo = SessionManager.findSession(msg.getSessionId());
            if (sessionInfo == null) {
                return;
            }

            switch (msg.getState()) {
                case IDLE:
                    handleMessageIdle(sessionInfo);
                    break;
                case OFFER:
                    handleMessageOffer(sessionInfo);
                    break;
                case ANSWER:
                    handleMessageAnswer(sessionInfo);
                    break;
                case PREPARE:
                    handleMessagePrepare(sessionInfo);
                    break;
                case READY:
                    handleMessageReady(sessionInfo);
                    break;
                case PLAY_B:
                    break;
                case PLAY_C:
                    break;
                case RELEASE:
                    handleMessageRelease(sessionInfo);
                    break;
            }
        }
    }

    private void handleMessageIdle(SessionInfo sessionInfo) {
        if (sessionInfo == null) {
            return;
        }

        if (sessionInfo.getServiceState() != SessionState.IDLE) {
            sessionInfo.setServiceState(SessionState.IDLE);
        }

        //
        // TODO
        //
    }


    private void handleMessageOffer(SessionInfo sessionInfo) {
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

    private void handleMessageAnswer(SessionInfo sessionInfo) {
        if (sessionInfo == null) {
            return;
        }

        if (sessionInfo.getServiceState() != SessionState.ANSWER) {
            sessionInfo.setServiceState(SessionState.ANSWER);
        }

        //
        // TODO
        //
    }

    private void handleMessagePrepare(SessionInfo sessionInfo) {
        if (sessionInfo == null) {
            return;
        }

        if (sessionInfo.getServiceState() != SessionState.PREPARE) {
            sessionInfo.setServiceState(SessionState.PREPARE);
        }

        //
        // TODO
        //
    }

    private void handleMessageReady(SessionInfo sessionInfo) {
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

    private void handleMessageRelease(SessionInfo sessionInfo) {
        if (sessionInfo == null) {
            return;
        }

        if (sessionInfo.getServiceState() != SessionState.RELEASE) {
            sessionInfo.setServiceState(SessionState.RELEASE);
            sessionInfo.updateT4Time(SessionManager.TIMER_HANGUP_T4);
        }

        sessionInfo.setLastSentTime();
        sessionInfo.updateT2Time(SessionManager.TIMER_HANGUP_T2);
    }
}
