package x3.player.mru.surfif.handler;

import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.common.JsonMessage;
import x3.player.mru.surfif.messages.SurfMsgSysInf;
import x3.player.mru.surfif.messages.SurfMsgSysInfData;

public class SurfProcSysInf {
    private static final Logger logger = LoggerFactory.getLogger(SurfProcSysInf.class);

    public SurfMsgSysInf parser(JsonElement element) {
        if (element == null) {
            return null;
        }

        SurfMsgSysInf msg = null;

        JsonMessage<SurfMsgSysInf> parser = new JsonMessage<>(SurfMsgSysInf.class);
        msg = parser.parse(element);

        if (msg == null) {
            return null;
        }

        if (msg.getInfType() == null || msg.getData() == null) {
            return msg;
        }

        SurfMsgSysInfData data = msg.getData();
        logger.debug("SysInf type {} data type {}", msg.getInfType(), data.getType());

        if (data.getType().equals("performance")) {
            parseStatusPerformance(data);
        }
        else if (data.getType().equals("network")) {
            parseStatusNetwork(data);
        }
        else if (data.getType().equals("video_performance")) {
            parseStatusVideoPerformance(data);
        }
        else if (data.getType().equals("file_reader_performance")) {
            parseStatusFileReaderPerformance(data);
        }
        else if (data.getType().equals("license")) {
            parseStatusLicense(data);
        }
        else {
            logger.warn("SysInf: Unknown data type {}", data.getType());
        }

        return msg;
    }

    private boolean parseStatusPerformance(SurfMsgSysInfData data) {
        if (data == null) {
            return false;
        }

        logger.debug("SysInf performance status:");
        if (data.getMemoryPools() != null) {
            for (int i = 0; i < data.getMemoryPools().size(); i++) {
                logger.debug("\tmemory_pools: block_size {} nof_free_blocks {} total_nof_blocks {}",
                        data.getMemoryPools().get(i).getBlockSize(),
                        data.getMemoryPools().get(i).getNofFreeBlocks(),
                        data.getMemoryPools().get(i).getTotalNofBlocks());
            }
        }
        if (data.getCpu() != null) {
            logger.debug("\tcpu: usage {} nof_late_sched_iterations {}",
                    data.getCpu().getCpuUsage(),
                    data.getCpu().getNofLateSchedIterations());
        }

        return true;
    }

    private boolean parseStatusNetwork(SurfMsgSysInfData data) {
        if (data == null) {
            return false;
        }

        logger.debug("SysInf network status: packet_loss {}", data.getPacketLoss());

        return true;
    }

    private boolean parseStatusVideoPerformance(SurfMsgSysInfData data) {
        if (data == null) {
            return false;
        }

        logger.debug("SysInf video_performance status: mixed_missed_frames {} decoder_missed_frames {} encoder_missed_frames {} gpu_usage {}",
                data.getMixerMissedFrames(), data.getDecoderMissedFrames(), data.getEncoderMissedFrames(), data.getGpuUsage());

        return true;
    }

    private boolean parseStatusFileReaderPerformance(SurfMsgSysInfData data) {
        if (data == null) {
            return false;
        }

        logger.debug("SysInf file_reader_performance status:");
        if (data.getFileReader() != null) {
            logger.debug("\tfile_reader: nof_iter {} total_dur {} nof_late_iter {} total_sleep {} min_sleep {} max_sleep {} avg_sleep {} usage {}",
                    data.getFileReader().getNofIterations(),
                    data.getFileReader().getTotalDuration(),
                    data.getFileReader().getNofLateIterations(),
                    data.getFileReader().getTotalSleepTime(),
                    data.getFileReader().getMinSleepTime(),
                    data.getFileReader().getMaxSleepTime(),
                    data.getFileReader().getAverageSleepTime(),
                    data.getFileReader().getUsage());
        }

        return true;
    }

    private boolean parseStatusLicense(SurfMsgSysInfData data) {
        if (data == null) {
            return false;
        }

        logger.debug("SysInf license status:");
        logger.debug("\tlicense: acodecs {} wb_codecs {} amixer {} play {} record {} srtp {} vcodecs {} vmixer {}",
                data.isAudioCodecs(), data.isAudioWBCodecs(), data.isAudioMixer(),
                data.isFilePlay(), data.isFileRecord(),
                data.isSrtp(),
                data.isVideoCodecs(), data.isVideoMixer());
        logger.debug("\tlicense: max_ep {} cur_ep {} max_vcodec_tools {} cur_vcodec_tools {} max_vres {}",
                data.getMaxEndpoints(), data.getCurEndpoints(),
                data.getMaxVideoCodecTools(), data.getCurVideoCodecTools(),
                data.getMaxVideoResolution());
        logger.debug("\tlicense: expiration_date {}", data.getExpirationDate());

        return true;
    }
}
