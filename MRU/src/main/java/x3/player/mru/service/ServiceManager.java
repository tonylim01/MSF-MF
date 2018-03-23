package x3.player.mru.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.AppInstance;
import x3.player.mru.common.NetUtil;
import x3.player.mru.config.AmfConfig;
import x3.player.mru.rmqif.messages.HeartbeatReq;
import x3.player.mru.rmqif.module.RmqClient;
import x3.player.mru.rmqif.module.RmqServer;
import x3.player.mru.session.SessionManager;

public class ServiceManager {

    private static final Logger logger = LoggerFactory.getLogger(ServiceManager.class);

    private RmqServer rmqServer;
    private SessionManager sessionManager;
    private HeartbeatManager heartbeatManager;

    private boolean isQuit = false;

    /**
     * Reads a config file in the constructor
     */
    public ServiceManager() {
        AppInstance instance = AppInstance.getInstance();
        instance.setConfig(new AmfConfig());
    }

    /**
     * Main loop
     */
    public void loop() {
        AmfConfig config = AppInstance.getInstance().getConfig();

        if (!pingRmqServer(config.getRmqHost())) {
            return;
        }

        startService();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            logger.warn("Process is about to quit (Ctrl+C)");
            isQuit = true;

            stopService();
            }));

        while (!isQuit) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("Process End");
    }

    /**
     * Returns a ping result to a rabbitmq server
     * @param host
     * @return
     */
    private boolean pingRmqServer(String host) {
        logger.info("Checking RMQ target [{}]", host);
        boolean rmqAvailable = NetUtil.ping(host, 1000);
        logger.info("Host [{}] is {}", host, rmqAvailable ? "reachable" : "NOT reachable");

        return rmqAvailable;
    }

    /**
     * Initializes pre-process
     * @return
     */
    private boolean startService() {
        rmqServer = new RmqServer();
        rmqServer.start();

        sessionManager = new SessionManager();
        sessionManager.start();

        heartbeatManager = HeartbeatManager.getInstance();
        heartbeatManager.start();

        return true;
    }

    /**
     * Finalizes all the resources
     */
    private void stopService() {
        if (rmqServer != null) {
            rmqServer.stop();
        }

        heartbeatManager.stop();
        sessionManager.stop();

        AmfConfig config = AppInstance.getInstance().getConfig();

        if (RmqClient.hasInstance(config.getMcudName())) {
            RmqClient.getInstance(config.getMcudName()).closeSender();
        }
    }
}
