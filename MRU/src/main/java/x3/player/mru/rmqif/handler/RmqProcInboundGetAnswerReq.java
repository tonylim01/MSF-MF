package x3.player.mru.rmqif.handler;

import x3.player.mru.AppInstance;
import x3.player.mru.config.AmfConfig;
import x3.player.mru.config.SdpConfig;
import x3.player.mru.rmqif.handler.base.RmqIncomingMessageHandler;
import x3.player.mru.rmqif.types.RmqMessage;
import x3.player.mru.session.SessionInfo;
import x3.player.mru.session.SessionState;
import x3.player.mru.session.SessionStateManager;
import x3.player.mru.simulator.UdpRelayManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcInboundGetAnswerReq extends RmqIncomingMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcInboundGetAnswerReq.class);

    @Override
    public boolean handle(RmqMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            logger.error("[{}] Invalid message");
            return false;
        }

        logger.info("[{}] InboundGetAnswerReq", msg.getSessionId());

        SessionInfo sessionInfo = validateSessionId(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());
        if (sessionInfo == null) {
            logger.error("[{}] Session not found", msg.getSessionId());
            return false;
        }

//        allocLocalResource(sessionInfo);

        SessionStateManager.getInstance().setState(msg.getSessionId(), SessionState.ANSWER);

        sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());

        return false;
    }

    @Override
    public void sendResponse(String sessionId, String transactionId, String queueName, int reasonCode, String reasonStr) {

        RmqProcInboundGetAnswerRes res = new RmqProcInboundGetAnswerRes(sessionId, transactionId);

        res.setReasonCode(reasonCode);
        res.setReasonStr(reasonStr);

        if (res.send(queueName) == false) {
            // TODO
        }
    }

    /**
     * Allocates a local media resource to receive RTP packets
     * @param sessionInfo
     * @return
     */
    private boolean allocLocalResource(SessionInfo sessionInfo) {
        if (sessionInfo == null) {
            return false;
        }

        if (sessionInfo.getSdpInfo() != null) {
            // Inbound
            // Returns a caller's sdp

        }
        else {
            // Outbound
            // Returns a callee's sdp
        }

        /***
         * Local relay demo
         */
        /*
        SdpConfig config = AppInstance.getInstance().getConfig().getSdpConfig();
        sessionInfo.setLocalIpAddress(config.getLocalIpAddress());

        UdpRelayManager udpRelayManager = UdpRelayManager.getInstance();
        int srcLocalPort = udpRelayManager.getNextLocalPort();
        int dstLocalPort = udpRelayManager.getNextLocalPort();

        sessionInfo.setSrcLocalPort(srcLocalPort);
        sessionInfo.setDstLocalPort(dstLocalPort);

        logger.debug("[{}] Alloc local media: ip [{}] srcPort [{}] dstPort [{}]", sessionInfo.getSessionId(),
                sessionInfo.getLocalIpAddress(), sessionInfo.getSrcLocalPort(), sessionInfo.getDstLocalPort());
        */

        return true;
    }
}

