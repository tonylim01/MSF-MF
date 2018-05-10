package x3.player.mru.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.App;
import x3.player.mru.AppInstance;
import x3.player.mru.common.ShellUtil;
import x3.player.mru.config.AmfConfig;
import x3.player.mru.rmqif.module.RmqClient;
import x3.player.mru.surfif.messages.SurfMsgVocoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

public class AiifRelay {

    private static final Logger logger = LoggerFactory.getLogger(AiifRelay.class);

    private static final int RTP_HEADER_SIZE = 12;

    private static boolean isQuit;

    private RmqClient rmqClient = null;
    private FileOutputStream fileStream = null;
    private RandomAccessFile inputPipeFile = null;
    private RandomAccessFile outputPipeFile = null;
    private boolean pipeOpened = false;
    private Thread ffmpegThread = null;
    private Thread rmqThread = null;
    private String inputPipeName;
    private String outputPipeName;

    private long audioDetectLevel = 0;
    private long silenceDetectLevel = 0;
    private long silenceDetectDuration = 0;

    private boolean isEnergyDetected = false;


    public void start() {
        isQuit = false;

        AmfConfig config = AppInstance.getInstance().getConfig();

        audioDetectLevel = config.getAudioEnergyLevel();
        silenceDetectLevel = config.getSilenceEnergyLevel();
        silenceDetectDuration = config.getSilenceDetectDuration();

        ffmpegThread = new Thread(new FfmpegRunnable());
        ffmpegThread.start();

        try {
            inputPipeFile = new RandomAccessFile(inputPipeName, "rw");
            if (inputCodec != null && inputCodec.equals(SurfMsgVocoder.VOCODER_AMR_WB)) {
                inputPipeFile.write(AMR_HEADER);
            }

            outputPipeFile = new RandomAccessFile(outputPipeName, "r");

            pipeOpened = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        rmqThread = new Thread(new RmqRelayRunnable());
        rmqThread.start();
    }

    public void stop() {
        isQuit = true;

        if (pipeOpened) {
            pipeOpened = false;

            if (transcodingProcess != null) {
                ShellUtil.killShell(transcodingProcess);
            }

            if (inputPipeFile != null) {

                logger.info("Close input pipe ({})", inputPipeName);
                try {
                    inputPipeFile.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                logger.info("Close input pipe ({}) done", inputPipeName);

                deleteFile(inputPipeName);

            }
            if (outputPipeFile != null) {

                logger.info("Close output pipe ({})", outputPipeName);
                try {
                    outputPipeFile.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                logger.info("Close output pipe ({}) done", outputPipeName);

                deleteFile(outputPipeName);
            }

        }

        if (fileStream != null) {
            try {
                fileStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (rmqThread != null) {
            rmqThread.interrupt();
            rmqThread = null;
        }
    }

    public boolean send(byte[] buf, int size) {
        if (buf == null || (buf != null && buf.length == 0)) {
            return false;
        }

        if (size > buf.length) {
            return false;
        }

        boolean result = false;

        if (inputPipeFile != null) {
            try {
                inputPipeFile.write(buf, RTP_HEADER_SIZE, size - RTP_HEADER_SIZE);
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }


    public void setRelayQueue(String queueName) {
        createPipe(queueName);
        rmqClient = RmqClient.getInstance(queueName);
    }

    private String inputCodec;

    public void setInputCodec(String codec) {
        inputCodec = codec;
    }

    private static final byte[] AMR_HEADER = { 0x23, 0x21, 0x41, 0x4D, 0x52, 0x2D, 0x57, 0x42, 0x0A };

    public void createPipe(String queueName) {
        inputPipeName = String.format("/tmp/%s_i", queueName);
        outputPipeName = String.format("/tmp/%s_o.wav", queueName);
        Process p;
        p = ShellUtil.createNamedPipe(inputPipeName);
        ShellUtil.waitShell(p);
        p = ShellUtil.createNamedPipe(outputPipeName);
        ShellUtil.waitShell(p);

    }

    public boolean deleteFile(String filename) {
        if (filename == null) {
            return false;
        }

        boolean result = false;
        File file = new File(filename);

        if (file.exists() && file.isFile()) {
            result = file.delete();
        }

        return result;
    }

    public void saveToFile(String filename) {
        try {
            fileStream = new FileOutputStream(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Process transcodingProcess = null;


    private long linearSum = 0;
    private int linearSumCount = 0;
    private short prevValue = 0;
    private long silenceStart;

    class FfmpegRunnable implements Runnable {
        @Override
        public void run() {

            logger.info("Ffmpeg proc ({}) start", inputPipeName);

            if (inputCodec != null && inputCodec.equals(SurfMsgVocoder.VOCODER_ALAW)) {
                transcodingProcess = ShellUtil.startAlawTranscoding(inputPipeName, outputPipeName);
            }
            else {
                transcodingProcess = ShellUtil.startAMRTranscoding(inputPipeName, outputPipeName);
            }

            ShellUtil.waitShell(transcodingProcess);

            logger.info("Ffmpeg proc ({}) end", inputPipeName);
        }
    }

    private static final int LINEAR_PAYLOAD_SIZE = 320;

    class RmqRelayRunnable implements Runnable {
        @Override
        public void run() {
            logger.info("Rmq relay proc ({}) start. queue [{}]", outputPipeName, (rmqClient != null) ? "yes" : "no");

            byte[] pipeBuf = new byte[LINEAR_PAYLOAD_SIZE];
            try {
                logger.info("Rmq relay proc ({}) ready. isQuit [{}]", outputPipeName, isQuit);

                while (!isQuit) {
                    int size = outputPipeFile.read(pipeBuf);
                    if (size > 0) {

                        if (rmqClient != null && rmqClient.isConnected()) {

                            rmqClient.send(pipeBuf, size);

                            // Just for log
                            if (fileStream != null) {
                                try {
                                    fileStream.write(pipeBuf, 0, size);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        if (audioDetectLevel > 0 && silenceDetectLevel > 0) {
                            energyDetect(pipeBuf, size, (rmqClient != null) ? true : false);
                        }

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            logger.info("Rmq relay proc ({}) end", outputPipeName);
        }

        private boolean energyDetect(byte[] buf, int length, boolean isCaller) {
            if (buf == null || length < RTP_HEADER_SIZE) {
                return false;
            }

            for (int i = RTP_HEADER_SIZE; i < length; i += 2) {
                short value = (short)((short)(((buf[i + 1] & 0xff) << 8) & 0xff00) | (short)(buf[i] & 0xff));

                if (value > 0 && value > prevValue) {
                    prevValue = value;
                }
                else if (value < 0 && prevValue > 0) {
                    linearSum += prevValue;
                    prevValue = 0;

                    if (linearSumCount >= 5) {
//                        logger.info("energy = {}", linearSum);
                        if (!isEnergyDetected && linearSum >= audioDetectLevel) {
                            //
                            // TODO: Voice detected
                            //
                            logger.info("Energy Detected [{}]", isCaller ? "caller" : "callee");

                            isEnergyDetected = true;
                        }
                        else if (isEnergyDetected) {
                            if (linearSum < silenceDetectLevel) {
                                //
                                // TODO: Silence detected
                                //
                                long timestamp = System.currentTimeMillis();
                                if (silenceStart == 0) {
                                    silenceStart = timestamp;
                                }
                                else if (timestamp - silenceStart > silenceDetectDuration) {
                                    logger.info("Silence Detected [{}]", isCaller ? "caller" : "callee");
                                    isEnergyDetected = false;
                                }
                            }
                            else if (silenceStart > 0) {
                                silenceStart = 0;
                            }
                        }

                        linearSumCount = 0;
                        linearSum = 0;

                    }
                }
            }

            linearSumCount++;

            return true;
        }
    }

}

