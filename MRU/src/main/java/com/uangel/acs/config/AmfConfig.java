package com.uangel.acs.config;

import com.uangel.acs.common.StringValue;
import com.uangel.core.config.DefaultConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class AmfConfig extends DefaultConfig {

    private static final Logger logger = LoggerFactory.getLogger(AmfConfig.class);
    private static final String CONFIG_FILE = "amf.conf";

    private String rmqHost;
    private String rmqLocal;
    private String rmqMcud;
    private String rmqUser, rmqPass;

    private int sessionMaxSize;
    private int sessionTimeout;

    private List<Integer> mediaPriorities;

    private SdpConfig sdpConfig;

    public AmfConfig() {

        super(CONFIG_FILE);

        boolean result = load();
        logger.info("Load config [{}] ... [{}]", CONFIG_FILE, StringValue.getOkFail(result));

        mediaPriorities = new ArrayList<>();
        sdpConfig = new SdpConfig();

        if (result == true) {
            loadConfig();
        }
    }

    @Override
    public String getStrValue(String key, String defaultValue) {
        String value = super.getStrValue(key, defaultValue);

        logger.info("\tConfig key [{}] value [{}]", key, value);
        return value;
    }

    private void loadConfig() {

        try {
            rmqHost = getStrValue("RMQ_HOST", "localhost");
            rmqLocal = getStrValue("RMQ_LOCAL", "localhost");
            rmqMcud = getStrValue("RMQ_MCUD", null);
            rmqUser = getStrValue("RMQ_USER", null);
            rmqPass = getStrValue("RMQ_PASS", null);

            sessionMaxSize = getIntValue("SESSION_MAX_SIZE", 0);
            sessionTimeout = getIntValue("SESSION_TIMEOUT_SEC", 0);

            String rawPasswd = getStrValue("RAW_PASS", null);
            if (rawPasswd != null) {
                String encoded = Base64.getEncoder().encodeToString(rawPasswd.getBytes());
                logger.warn("Encoding password: input [{}] encoded [{}]", rawPasswd, encoded);

            }

            if (rmqPass != null) {
                String decoded = new String(Base64.getDecoder().decode(rmqPass));
                logger.info("Decoding password: input [{}] decoded [{}]", rmqPass, decoded);
                rmqPass = decoded;
            }

            String mediaPriority = getStrValue("MEDIA_PRIORITY", null);
            if (mediaPriority != null) {
                setMediaPriority(mediaPriority);
            }

            String localHost = getStrValue("SDP_LOCAL_HOST", null);
            String localIp = getStrValue("SDP_LOCAL_IP", null);

            sdpConfig.setLocalHost(localHost);
            sdpConfig.setLocalIpAddress(localIp);

            for (int i = 0; ; i++) {
                String key = String.format("SDP_LOCAL_ATTR_%d", i);
                String attr = getStrValue(key, null);
                if (attr == null) {
                    break;
                }
                sdpConfig.addAttribute(attr);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getRmqHost() {
        return rmqHost;
    }

    public String getLocalName() {
        return rmqLocal;
    }

    public String getMcudName() {
        return rmqMcud;
    }

    public String getRmqUser() {
        return rmqUser;
    }

    public String getRmqPass() {
        return rmqPass;
    }

    public SdpConfig getSdpConfig() {
        return sdpConfig;
    }

    public int getSessionMaxSize() {
        return sessionMaxSize;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    private void setMediaPriority(String priorityStr) {
        if (priorityStr == null) {
            return;
        }

        String[] priorities = priorityStr.split(",");
        if (priorities != null) {
            for (String priority: priorities) {
                try {
                    mediaPriorities.add(Integer.valueOf(priority.trim()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Integer> getMediaPriorities() {
        return mediaPriorities;
    }

    public int getMediaPriority(int index) {
        if (index < 0 || index >= mediaPriorities.size()) {
            return -1;
        }

        return mediaPriorities.get(index);
    }
}
