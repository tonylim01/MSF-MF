package x3.player.mru.surfif.messages;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SurfMsgSysInfData {
    private String type;

    /**
     * Network status
     */
    @SerializedName("packet_loss")
    private int packetLoss;

    /**
     * Video_performance status
     */
    @SerializedName("mixer_missed_frames")
    private int mixerMissedFrames;
    @SerializedName("decoder_missed_frames")
    private int decoderMissedFrames;
    @SerializedName("encoder_missed_frames")
    private int encoderMissedFrames;
    @SerializedName("GPU_usage")
    private int gpuUsage;

    /**
     * File_reader_performance status
     */
    @SerializedName("file_Reader")
    private SurfFileReader fileReader;

    /**
     * Performace status
     */
    @SerializedName("memory_pools")
    private List<SurfMemoryPool> memoryPools;
    @SerializedName("CPU")
    private SurfCpu cpu;

    /**
     * License status
     */
    @SerializedName("audio_codecs")
    private boolean audioCodecs;
    @SerializedName("audio_wb_codecs")
    private boolean audioWBCodecs;
    @SerializedName("audio_mixer")
    private boolean audioMixer;
    @SerializedName("file_play")
    private boolean filePlay;
    @SerializedName("file_record")
    private boolean fileRecord;
    @SerializedName("SRTP")
    private boolean srtp;
    @SerializedName("video_codecs")
    private boolean videoCodecs;
    @SerializedName("video_mixer")
    private boolean videoMixer;
    @SerializedName("max_end_points")
    private int maxEndpoints;
    @SerializedName("cur_end_points")
    private int curEndpoints;
    @SerializedName("max_video_codec_tools")
    private int maxVideoCodecTools;
    @SerializedName("cur_video_codec_tools")
    private int curVideoCodecTools;
    @SerializedName("max_video_resolution")
    private String maxVideoResolution;
    @SerializedName("expiration_date")
    private String expirationDate;

    /**
     * Defines sub-classes
     */
    public class SurfMemoryPool {
        @SerializedName("block_size")
        private int blockSize;
        @SerializedName("nof_free_blocks")
        private int nofFreeBlocks;
        @SerializedName("total_nof_blocks")
        private int totalNofBlocks;

        public int getBlockSize() {
            return blockSize;
        }

        public int getNofFreeBlocks() {
            return nofFreeBlocks;
        }

        public int getTotalNofBlocks() {
            return totalNofBlocks;
        }
    }

    public class SurfCpu {
        @SerializedName("CPU_usage")
        private int cpuUsage;
        @SerializedName("nof_late_sched_iterations")
        private int nofLateSchedIterations;

        public int getCpuUsage() {
            return cpuUsage;
        }

        public int getNofLateSchedIterations() {
            return nofLateSchedIterations;
        }
    }

    public class SurfFileReader {
        @SerializedName("nof_iterations")
        private int nofIterations;
        @SerializedName("total_duration")
        private int totalDuration;
        @SerializedName("nof_late_iterations")
        private int nofLateIterations;
        @SerializedName("total_sleep_time")
        private int totalSleepTime;
        @SerializedName("min_sleep_time")
        private int minSleepTime;
        @SerializedName("max_sleep_time")
        private int maxSleepTime;
        @SerializedName("average_sleep_time")
        private int averageSleepTime;
        private int usage;

        public int getNofIterations() {
            return nofIterations;
        }

        public int getTotalDuration() {
            return totalDuration;
        }

        public int getNofLateIterations() {
            return nofLateIterations;
        }

        public int getTotalSleepTime() {
            return totalSleepTime;
        }

        public int getMinSleepTime() {
            return minSleepTime;
        }

        public int getMaxSleepTime() {
            return maxSleepTime;
        }

        public int getAverageSleepTime() {
            return averageSleepTime;
        }

        public int getUsage() {
            return usage;
        }
    }

    public String getType() {
        return type;
    }

    public int getPacketLoss() {
        return packetLoss;
    }

    public int getMixerMissedFrames() {
        return mixerMissedFrames;
    }

    public int getDecoderMissedFrames() {
        return decoderMissedFrames;
    }

    public int getEncoderMissedFrames() {
        return encoderMissedFrames;
    }

    public int getGpuUsage() {
        return gpuUsage;
    }

    public SurfFileReader getFileReader() {
        return fileReader;
    }

    public List<SurfMemoryPool> getMemoryPools() {
        return memoryPools;
    }

    public SurfCpu getCpu() {
        return cpu;
    }

    public boolean isAudioCodecs() {
        return audioCodecs;
    }

    public boolean isAudioWBCodecs() {
        return audioWBCodecs;
    }

    public boolean isAudioMixer() {
        return audioMixer;
    }

    public boolean isFilePlay() {
        return filePlay;
    }

    public boolean isFileRecord() {
        return fileRecord;
    }

    public boolean isSrtp() {
        return srtp;
    }

    public boolean isVideoCodecs() {
        return videoCodecs;
    }

    public boolean isVideoMixer() {
        return videoMixer;
    }

    public int getMaxEndpoints() {
        return maxEndpoints;
    }

    public int getCurEndpoints() {
        return curEndpoints;
    }

    public int getMaxVideoCodecTools() {
        return maxVideoCodecTools;
    }

    public int getCurVideoCodecTools() {
        return curVideoCodecTools;
    }

    public String getMaxVideoResolution() {
        return maxVideoResolution;
    }

    public String getExpirationDate() {
        return expirationDate;
    }
}
