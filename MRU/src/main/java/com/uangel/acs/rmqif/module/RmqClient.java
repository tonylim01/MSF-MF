package com.uangel.acs.rmqif.module;

import com.uangel.acs.AppInstance;
import com.uangel.acs.config.AmfConfig;
import com.uangel.core.rabbitmq.transport.RmqSender;

public class RmqClient {

    private static RmqClient client = null;

    public static RmqClient getInstance() {
        if (client == null) {
            client = new RmqClient();
        }
        return client;
    }

    private RmqSender sender = null;
    private boolean isConnected = false;

    public RmqClient() {
        isConnected = createSender();
    }

    private boolean createSender() {
        AmfConfig config = AppInstance.getInstance().getConfig();
        sender = new RmqSender(config.getRmqHost(), config.getRmqUser(), config.getRmqPass(), config.getMcudName());
        return sender.connect();
   }

   private void closeSender() {
        if (sender != null) {
            sender.close();
            sender = null;
        }
   }

   private boolean send(String msg) {
        boolean result = false;

        if (sender != null) {
            result = sender.send(msg);
        }

        return result;
   }
}
