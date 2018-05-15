package x3.player.mru.config;

import x3.player.mru.common.NetUtil;
import x3.player.mru.common.StringUtil;
import x3.player.core.config.DefaultConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class AmfConfig extends DefaultConfig {

    private static final Logger logger = LoggerFactory.getLogger(AmfConfig.class);

    private String rmqHost;
    private String rmqLocal;
    private String rmqMcud;
    private String rmqAcswf;
    private String rmqUser, rmqPass;
    private String rmqAiifs[];
    private String rmqAiifFmt;

    private int sessionMaxSize;
    private int sessionTimeout;

    private List<Integer> mediaPriorities;

    private SdpConfig sdpConfig;

    private int localUdpPortMin;
    private int localUdpPortMax;
    private String localNetInterface;
    private String localIpAddress;

    private SurfConfig surfConfig;

    private String localBasePath;
    private long audioEnergyLevel;
    private long silenceEnergyLevel;
    private long silenceDetectDuration;
    private long energyDetectDuration;

    private String promptConfPath;

    public AmfConfig(int instanceId, String configPath) {

        super(configPath);

        boolean result = load();
        logger.info("Load config [{}] ... [{}]", configPath, StringUtil.getOkFail(result));

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

        loadCommonConfig();
        loadSessionConfig();
        loadRmqConfig(instanceSection);
        loadSurfConfig(instanceSection);
        loadMediaConfig(instanceSection);
    }

    private void loadCommonConfig() {
        try {
            promptConfPath = getStrValue("COMMON", "PROMPT_CONF_PATH", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

            String rmqAiif = getStrValue("RMQ", "RMQ_AIIF", null);
            if (rmqAiif != null && rmqAiif.contains(",")) {
                String[] aiifs = rmqAiif.split(",");
                if (aiifs != null) {
                    rmqAiifs = new String[aiifs.length];
                    for (int i = 0; i < aiifs.length; i++) {
                        rmqAiifs[i] = aiifs[i].trim();
                    }
                }
            }

            rmqAiifFmt = getStrValue("RMQ", "RMQ_AIIF_FMT", null);

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

            int totalChannels = getIntValue("SURF", "TOTAL_CHANNELS", 0);

            surfConfig.setTotalChannels(totalChannels);

            String internalCodec = getStrValue("SURF", "INTERNAL_CODEC",
                    SurfConfig.DEFAULT_INTERNAL_CODEC);
            int internalPayload = getIntValue("SURF", "INTERNAL_PAYLOAD", 0);
            int internalSampleRate = getIntValue("SURF", "INTERNAL_SAMPLE_RATE",
                    SurfConfig.DEFAULT_INTERNAL_SAMPLE_RATE);

            surfConfig.setInternalCodec(internalCodec);
            surfConfig.setInternalPayload(internalPayload);
            surfConfig.setInternalSampleRate(internalSampleRate);

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

            localNetInterface = getStrValue("MEDIA", "LOCAL_NET_INTERFACE", null);

            if (localNetInterface != null) {
                localIpAddress = NetUtil.getLocalIP(localNetInterface);
            }
            else {
                logger.error("Local IP not found for [{}]", localNetInterface);
            }

            localBasePath = getStrValue("MEDIA", "LOCAL_BASE_PATH", null);

            audioEnergyLevel = (long)getIntValue("MEDIA", "AUDIO_ENERGY_LEVEL", 0);
            silenceEnergyLevel = (long)getIntValue("MEDIA", "SILENCE_ENERGY_LEVEL", 0);
            silenceDetectDuration = (long)getIntValue("MEDIA", "SILENCE_DETECT_DURATION", 0);
            energyDetectDuration = (long)getIntValue("MEDIA", "ENERGY_DETECT_DURATION", 0);

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

    public String getLocalNetInterface() {
        return localNetInterface;
    }

    public String getLocalIpAddress() {
        return localIpAddress;
    }

    public String getRmqAiif(int index) {
        if (rmqAiifs == null || index < 0 || index >= rmqAiifs.length) {
            return null;
        }

        return rmqAiifs[index];
    }

    public String getRmqAiifFmt() {
        return this.rmqAiifFmt;
    }

    public String getLocalBasePath() {
        return localBasePath;
    }

    public long getAudioEnergyLevel() {
        return audioEnergyLevel;
    }

    public long getSilenceEnergyLevel() {
        return silenceEnergyLevel;
    }

    public long getSilenceDetectDuration() {
        return silenceDetectDuration;
    }

    public long getEnergyDetectDuration() {
        return energyDetectDuration;
    }

    public String getPromptConfPath() {
        return promptConfPath;
    }
}
