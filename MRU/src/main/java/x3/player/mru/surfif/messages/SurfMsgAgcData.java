package x3.player.mru.surfif.messages;

import com.google.gson.annotations.SerializedName;

public class SurfMsgAgcData {

    private Boolean enabled;
    @SerializedName("energy_avg_window")
    private Integer energyAvgWindow;
    @SerializedName("min_signal_level")
    private Integer minSignalLevel;
    @SerializedName("max_signal_level")
    private Integer maxSignalLevel;
    @SerializedName("step_level")
    private Integer stepLevel;
    @SerializedName("silence_threshold")
    private Integer silenceThreshold;
    @SerializedName("limit_gain")
    private Boolean limitGain;
    @SerializedName("max_gain")
    private Integer maxGain;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setEnergyAvgWindow(int energyAvgWindow) {
        this.energyAvgWindow = energyAvgWindow;
    }

    public void setMinSignalLevel(int minSignalLevel) {
        this.minSignalLevel = minSignalLevel;
    }

    public void setMaxSignalLevel(int maxSignalLevel) {
        this.maxSignalLevel = maxSignalLevel;
    }

    public void setStepLevel(int stepLevel) {
        this.stepLevel = stepLevel;
    }

    public void setSilenceThreshold(int silenceThreshold) {
        this.silenceThreshold = silenceThreshold;
    }

    public void setLimitGain(boolean limitGain) {
        this.limitGain = limitGain;
    }

    public void setMaxGain(int maxGain) {
        this.maxGain = maxGain;
    }
}
