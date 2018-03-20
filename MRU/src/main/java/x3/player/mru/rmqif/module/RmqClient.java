package x3.player.mru.rmqif.module;

import x3.player.mru.AppInstance;
import x3.player.mru.config.AmfConfig;
import x3.player.core.rabbitmq.transport.RmqSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RmqClient {

    private static final Logger logger = LoggerFactory.getLogger(RmqClient.class);

    private static Map<String, RmqClient> clients = null;

    public static RmqClient getInstance(String queueName) {
        if (clients == null) {
            clients = new HashMap<>();
        }

        RmqClient client = clients.get(queueName);
        if (client == null) {
            client = new RmqClient(queueName);
            clients.put(queueName, client);
        }

        return client;
    }

    public static boolean hasInstance(String queueName) {
        if (clients == null) {
            return false;
        }

        return clients.containsKey(queueName);
    }

    private RmqSender sender = null;
    private boolean isConnected = false;
    private String queueName = null;

    public RmqClient(String queueName) {

        this.queueName = queueName;
        this.isConnected = createSender(queueName);
    }

    private boolean createSender(String queueName) {
        AmfConfig config = AppInstance.getInstance().getConfig();
        if (config == null) {
            return false;
        }
        sender = new RmqSender(config.getRmqHost(), config.getRmqUser(), config.getRmqPass(), queueName);
        return sender.connect();
   }

   public void closeSender() {
        if (sender != null) {
            sender.close();
            sender = null;
        }
   }

   public boolean send(String msg) {
        if (sender == null) {
            if (createSender(queueName) == false) {
                return false;
            }
            if (sender == null) {
                return false;
            }
        }

        if (!sender.isOpened()) {
            if (!sender.connect()) {
                return false;
            }
        }

        return sender.send(msg);
        }
}
