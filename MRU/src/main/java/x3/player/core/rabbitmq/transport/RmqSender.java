package x3.player.core.rabbitmq.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqSender extends RmqTransport {

    private static final Logger logger = LoggerFactory.getLogger(RmqSender.class);

    public RmqSender(String host, String userName, String password, String queueName) {
        super(host, userName, password, queueName);
    }

    public boolean send(String msg) {

        if (getChannel().isOpen() == false) {
            logger.error("RMQ channel is NOT opened");
            return false;
        }

        boolean result = false;

        try {
            getChannel().basicPublish("", getQueueName(), null, msg.getBytes());
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean isOpened() {
        return getChannel().isOpen();
    }
}
