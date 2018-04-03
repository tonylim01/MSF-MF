package x3.player.mru.config;

import x3.player.mru.common.StringUtil;
import x3.player.core.config.DefaultConfig;
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
    private String rmqAcswf;
    private String rmqUser, rmqPass;

    private int sessionMaxSize;
    private int sessionTimeout;

    private List<Integer> mediaPriorities;

    private SdpConfig sdpConfig;

    private int localUdpPortMin;
    private int localUdpPortMax;

    private SurfConfig surfConfig;

    public AmfConfig(int instanceId) {

        super(CONFIG_FILE);

        boolean result = load();
        logger.info("Load config [{}] ... [{}]", CONFIG_FILE, StringUtil.getOkFail(result));

        mediaPriorities = new ArrayList<>();
        sdpConfig = new SdpConfig();
        surfConfig = new SurfConfig();

        if (result == true) {
            loadConfig(instanceId);
        }
    }

    @Override
    public String getStrValue(String session, String key, String defaultValue) {
        String value = super.getStrValue(session, key, defaultValue);

        logger.info("\tConfig key [{}] value [{}]", key, value);
        return value;
    }

    private void loadConfig(int instanceId) {

        String instanceSection = String.format("INSTANCE-%d", instanceId);

        loadSessionConfig();
        loadRmqConfig(instanceSection);
        loadSurfConfig(instanceSection);
        loadMediaConfig(instanceSection);
    }

    private void loadSessionConfig() {
        try {
            sessionMaxSize = getIntValue("SESSION", "SESSION_MAX_SIZE", 0);
            sessionTimeout = getIntValue("SESSION", "SESSION_TIMEOUT_SEC", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRmqConfig(String instanceSection) {
        try {
            rmqHost = getStrValue("RMQ", "RMQ_HOST", "localhost");
            rmqMcud = getStrValue("RMQ", "RMQ_MCUD", null);
            rmqAcswf = getStrValue("RMQ", "RMQ_ACSWF", null);
            rmqUser = getStrValue("RMQ", "RMQ_USER", null);
            rmqPass = getStrValue("RMQ", "RMQ_PASS", null);

            String rawPasswd = getStrValue("RMQ", "RAW_PASS", null);
            if (rawPasswd != null) {
                String encoded = Base64.getEncoder().encodeToString(rawPasswd.getBytes());
                logger.warn("Encoding password: input [{}] encoded [{}]", rawPasswd, encoded);

            }

            if (rmqPass != null) {
                String decoded = new String(Base64.getDecoder().decode(rmqPass));
                logger.info("Decoding password: input [{}] decoded [{}]", rmqPass, decoded);
                rmqPass = decoded;
            }

            rmqLocal = getStrValue(instanceSection, "RMQ_LOCAL", "localhost");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSurfConfig(String instanceSection) {
        try {

            String surfIp = getStrValue(instanceSection, "SURF_IP", "localhost");
            int surfPort = getIntValue(instanceSection, "SURF_PORT", 0);

            surfConfig.setSurfIp(surfIp);
            surfConfig.setSurfPort(surfPort);

            int surfMajorVersion = getIntValue("SURF", "MAJOR_VERSION", 0);
            int surfMinorVersion = getIntValue("SURF", "MINOR_VERSION", 0);
            int keepAliveTime = getIntValue("SURF", "KEEP_ALIVE_TIME", 0);

            surfConfig.setMajorVersion(surfMajorVersion);
            surfConfig.setMinorVersion(surfMinorVersion);
            surfConfig.setKeepAliveTime(keepAliveTime);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMediaConfig(String instanceSection) {
        try {
            String mediaPriority = getStrValue("MEDIA", "MEDIA_PRIORITY", null);
            if (mediaPriority != null) {
                setMediaPriority(mediaPriority);
            }

            String localHost = getStrValue("MEDIA", "SDP_LOCAL_HOST", null);
            String localIp = getStrValue("MEDIA", "SDP_LOCAL_IP", null);

            sdpConfig.setLocalHost(localHost);
            sdpConfig.setLocalIpAddress(localIp);

            for (int i = 0; ; i++) {
                String key = String.format("SDP_LOCAL_ATTR_%d", i);
                String attr = getStrValue("MEDIA", key, null);
                if (attr == null) {
                    break;
                }
                sdpConfig.addAttribute(attr);
            }

            localUdpPortMin = getIntValue(instanceSection, "LOCAL_UDP_PORT_MIN", 0);
            localUdpPortMax = getIntValue(instanceSection, "LOCAL_UDP_PORT_MAX", 0);
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

    public String getRmqAcswf() {
        return rmqAcswf;
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

    public int getLocalUdpPortMin() {
        return localUdpPortMin;
    }

    public int getLocalUdpPortMax() {
        return localUdpPortMax;
    }

    public SurfConfig getSurfConfig() {
        return surfConfig;
    }

    public String getSurfIp() {
        return surfConfig.getSurfIp();
    }

    public int getSurfPort() {
        return surfConfig.getSurfPort();
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
