package com.uangel.acs.rmqif.module;

import com.uangel.acs.AppInstance;
import com.uangel.acs.common.StringValue;
import com.uangel.acs.config.AmfConfig;
import com.uangel.core.rabbitmq.transport.RmqCallback;
import com.uangel.core.rabbitmq.transport.RmqReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class RmqServer {
    private static final Logger logger = LoggerFactory.getLogger(RmqServer.class);

    private static final int QUEUE_SIZE = 8;

    private RmqReceiver receiver = null;
    private BlockingQueue<String> queue;
    private Thread rmqConsumerThread;

    public RmqServer() {
        queue = new ArrayBlockingQueue<>(QUEUE_SIZE);
        rmqConsumerThread = new Thread(new RmqConsumer(queue));
    }

    public void start() {
        AmfConfig config = AppInstance.getInstance().getConfig();

        logger.info("{} start", getClass().getSimpleName());

        rmqConsumerThread.start();

        receiver = new RmqReceiver(config.getRmqHost(), config.getRmqUser(), config.getRmqPass(), config.getLocalName());
        receiver.setCallback(new MessageCallback());

        boolean result = receiver.connect();
        logger.info("{} connect ... [{}]", getClass().getSimpleName(), StringValue.getOkFail(result));

        if (result == false) {
            return;
        }

        result = receiver.start();
        logger.info("{} [{}] start ... [{}]", getClass().getSimpleName(), config.getLocalName(), StringValue.getOkFail(result));
    }

    public void stop() {
        receiver.close();
        rmqConsumerThread.interrupt();
        rmqConsumerThread = null;
    }

    private class MessageCallback implements RmqCallback {
        @Override
        public void onReceived(String msg) {
            logger.info("onReceived : {}", msg);

            if (msg != null) {
                try {
                    queue.put(msg);
                } catch (Exception e) {
                    e.getClass();
                }
            }
        }
    }
}
