package x3.player.mru.surfif.messages;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SurfMsgToolReqData {
    /**
     * Voice channel
     */
    @SerializedName("tool_type")
    private String toolType;
    @SerializedName("backend_tool_id")
    private Integer backendToolId;
    private SurfMsgVocoder decoder;
    private SurfMsgVocoder encoder;
    @SerializedName("RTP")
    private SurfMsgRtp rtp;
    @SerializedName("input_from_RTP")
    private Boolean inputFromRtp;

    @SerializedName("app_info")
    private String appInfo;

    @SerializedName("EVG")
    private SurfMsgEvg evg;

    /**
     * Events
     */
    private List<SurfMsgEvent> events;

    /**
     * Status
     */
    private List<SurfMsgStatus> status;

    /**
     * Mixer
     */
    @SerializedName("sampling_rate")
    private Integer samplingRate;
    @SerializedName("hangover_period")
    private Integer hangoverPeriod;
    @SerializedName("dominant_speakers")
    private Integer dominantSpeakers;

    /**
     * file_reader
     */
    @SerializedName("audio_enabled")
    private Boolean audioEnabled;
    @SerializedName("audio_dst_tool_ids")
    private List<Integer> audioDstToolIds;

    /**
     * Commands
     * play_list_append
     */
    @SerializedName("cmd_type")
    private String cmdType;
    private List<SurfMsgFile> files;
    private Integer repetitions;
    private Float duration;     // Seconds

    @SerializedName("AGC")
    private SurfMsgAgc agc;

    private List<SurfMsgParticipant> participants;

    public SurfMsgToolReqData() {
    }

    public String getToolType() {

        return toolType;
    }

    public void setToolType(String toolType) {
        this.toolType = toolType;
    }

    public int getBackendToolId() {
        return backendToolId;
    }

    public void setBackendToolId(int backendToolId) {
        this.backendToolId = backendToolId;
    }

    public SurfMsgVocoder getDecoder() {
        return decoder;
    }

    public void newDecoder() {
        this.decoder = new SurfMsgVocoder();
    }

    public void setDecoder(SurfMsgVocoder decoder) {
        this.decoder = decoder;
    }

    public SurfMsgVocoder getEncoder() {
        return encoder;
    }

    public void newEncoder() {
        this.encoder = new SurfMsgVocoder();
    }

    public void setEncoder(SurfMsgVocoder encoder) {
        this.encoder = encoder;
    }

    public SurfMsgRtp getRtp() {
        return rtp;
    }

    public void setRtp(SurfMsgRtp rtp) {
        this.rtp = rtp;
    }

    public void newRtp() {
        this.rtp = new SurfMsgRtp();
    }

    public SurfMsgEvg getEvg() {
        return evg;
    }

    public void setEvg(SurfMsgEvg evg) {
        this.evg = evg;
    }

    public void newEvg() {
        this.evg = new SurfMsgEvg();
    }

    public Boolean getInputFromRtp() {
        return inputFromRtp;
    }

    public void setInputFromRtp(Boolean inputFromRtp) {
        this.inputFromRtp = inputFromRtp;
    }

    public String getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(String appInfo) {
        this.appInfo = appInfo;
    }

    public int getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(int samplingRate) {
        this.samplingRate = samplingRate;
    }

    public int getHangoverPeriod() {
        return hangoverPeriod;
    }

    public void setHangoverPeriod(int hangoverPeriod) {
        this.hangoverPeriod = hangoverPeriod;
    }

    public int getDominantSpeakers() {
        return dominantSpeakers;
    }

    public void setDominantSpeakers(int dominantSpeakers) {
        this.dominantSpeakers = dominantSpeakers;
    }

    public String getCmdType() {
        return cmdType;
    }

    public void setCmdType(String cmdType) {
        this.cmdType = cmdType;
    }

    public void addFile(String name, float duration, String format, int segment) {
        if (this.files == null) {
            this.files = new ArrayList<>();
        }

        SurfMsgFile file = new SurfMsgFile();

        if (name != null) {
            file.setName(name);
        }

        if (duration != 0) {
            file.setDuration(duration);
        }

        if (format != null) {
            file.setFormat(format);
        }

        if (segment != 0) {
            file.setSegment(segment);
        }

        this.files.add(file);
    }

    public void addParticipant(int id, String type, int toolId, int whisperTo, String action) {
        if (this.participants == null) {
            this.participants = new ArrayList<>();
        }

        SurfMsgParticipant par = new SurfMsgParticipant();

        par.setId(id);
        if (type != null) {
            par.setType(type);
        }

        par.setToolId(toolId);
        if (whisperTo >= 0) {
            par.setWhisperTo(whisperTo);
        }
        if (action != null) {
            par.setAction(action);
        }

        this.participants.add(par);
    }

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public boolean getAudioEnabled() {
        return audioEnabled;
    }

    public void setAudioEnabled(boolean audioEnabled) {
        this.audioEnabled = audioEnabled;
    }

    public List<Integer> getAudioDstToolIds() {
        return audioDstToolIds;
    }

    public void setAudioDstToolId(int audioDstToolId) {
        if (this.audioDstToolIds == null) {
            this.audioDstToolIds = new ArrayList<>();
        }

        this.audioDstToolIds.add(audioDstToolId);
    }

    public void setAgcEncoder(boolean enabled,
                              int energyAvgWindow, int minSignalLevel, int maxSignalLevel, int stepLevel,
                              int silenceThreshold) {

        SurfMsgAgcData data = getAgcData(enabled, energyAvgWindow, minSignalLevel, maxSignalLevel, stepLevel,
                silenceThreshold);

        if (this.agc == null) {
            this.agc = new SurfMsgAgc();
        }

        this.agc.setEncoderSide(data);
    }

    public void setAgcDecoder(boolean enabled,
                              int energyAvgWindow, int minSignalLevel, int maxSignalLevel, int stepLevel,
                              int silenceThreshold) {

        SurfMsgAgcData data = getAgcData(enabled, energyAvgWindow, minSignalLevel, maxSignalLevel, stepLevel,
                silenceThreshold);

        if (this.agc == null) {
            this.agc = new SurfMsgAgc();
        }

        this.agc.setDecoderSide(data);
    }

    public void setVad(boolean enabled) {
        if (this.getEncoder() != null) {
            this.getEncoder().setVad(enabled, null, false);
        }
    }

    private SurfMsgAgcData getAgcData(boolean enabled,
                                      int energyAvgWindow, int minSignalLevel, int maxSignalLevel, int stepLevel,
                                      int silenceThreshold) {
        SurfMsgAgcData data = new SurfMsgAgcData();

        data.setEnabled(enabled);
        if (energyAvgWindow != 0) {
            data.setEnergyAvgWindow(energyAvgWindow);
        }
        if (minSignalLevel != 0) {
            data.setMinSignalLevel(minSignalLevel);
        }
        if (maxSignalLevel != 0) {
            data.setMaxSignalLevel(maxSignalLevel);
        }
        if (stepLevel > 0) {
            data.setStepLevel(stepLevel);
        }
        if (silenceThreshold != 0) {
            data.setSilenceThreshold(silenceThreshold);
        }
//        data.setLimitGain(limitGain);
//        data.setMaxGain(maxGain);

        return data;
    }

    public void addEvent(String type, boolean enabled) {
        if (this.events == null) {
            this.events = new ArrayList<>();
        }

        SurfMsgEvent event = new SurfMsgEvent();
        event.setType(type);
        event.setEnabled(enabled);

        this.events.add(event);
    }

    public void addStatus(String type, int period) {
        if (this.status == null) {
            this.status= new ArrayList<>();
        }

        SurfMsgStatus status = new SurfMsgStatus();
        status.setType(type);
        status.setPeriod(period);

        this.status.add(status);
    }
}
