package x3.player.core.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.mru.common.ShellUtil;
import x3.player.mru.rmqif.module.RmqClient;
import x3.player.mru.surfif.messages.SurfMsgVocoder;
import x3.player.mru.surfif.types.SurfConstant;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpSocket {

    private static final Logger logger = LoggerFactory.getLogger(UdpServer.class);

    private static final int MAX_BUFFER_SIZE = 4096;
    private static final int RTP_HEADER_SIZE = 12;

    private DatagramSocket socket;
    private Thread thread = null;
    private byte[] buf = new byte[MAX_BUFFER_SIZE];
    private boolean isQuit = false;
    private InetAddress address;
    private int localPort;
    private int remotePort;

    private UdpSocket remoteSocket;

    private RmqClient rmqClient = null;
    private FileOutputStream fileStream = null;
    private RandomAccessFile inputPipeFile = null;
    private RandomAccessFile outputPipeFile = null;
    private boolean pipeOpened = false;
    private Thread ffmpegThread = null;
    private Thread rmqThread = null;
    private String inputPipeName;
    private String outputPipeName;

    public UdpSocket(String ipAddress, int remotePort, int localPort) {
        try {
            socket = new DatagramSocket(localPort);
            address = InetAddress.getByName(ipAddress);
            logger.debug("UdpSocket ip {} port {}", socket.getLocalSocketAddress().toString(), localPort);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.remotePort = remotePort;
        this.localPort = localPort;
    }

    private UdpCallback callback = null;

    public void setUdpCallback(UdpCallback callback) {
        this.callback = callback;
    }

    public boolean start() {
        if (thread != null) {
            return false;
        }

        thread = new Thread(new UdpServerRunnable());
        thread.start();

        return true;
    }

    public void stop() {
        if (pipeOpened) {
            pipeOpened = false;
            if (inputPipeFile != null) {

                logger.info("Close input pipe ({})", inputPipeName);
                try {
                    inputPipeFile.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (outputPipeFile != null) {

                logger.info("Close output pipe ({})", outputPipeName);
                try {
                    outputPipeFile.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

        if (thread != null) {
            thread.interrupt();
            thread = null;
        }

        socket.close();
    }

    public UdpSocket getRemoteSocket() {
        return remoteSocket;
    }

    public void setRemoteSocket(UdpSocket remoteSocket) {
        this.remoteSocket = remoteSocket;
    }

    public boolean send(byte[] buf, int size) {
        if (socket == null) {
            return false;
        }

        if (buf == null || (buf != null && buf.length == 0)) {
            return false;
        }

        if (size > buf.length) {
            return false;
        }

        boolean result = false;
        DatagramPacket packet = new DatagramPacket(buf, size, address, remotePort);
        try {
            socket.send(packet);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (buf != null && size > 0) {
            if (inputPipeFile != null) {
                try {
                    inputPipeFile.write(buf, RTP_HEADER_SIZE, size - RTP_HEADER_SIZE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    private void createPipe(String queueName) {
        inputPipeName = String.format("/tmp/%s_i", queueName);
        outputPipeName = String.format("/tmp/%s_o.wav", queueName);
        ShellUtil.createNamedPipe(inputPipeName);
        ShellUtil.createNamedPipe(outputPipeName);

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


    public void saveToFile(String filename) {
        try {
            fileStream = new FileOutputStream(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class UdpServerRunnable implements Runnable {
        @Override
        public void run() {

            logger.info("UdpSocket server ({}) start", localPort);
            while (!isQuit) {
                try {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    if (callback != null) {
                        callback.onReceived(packet.getAddress().getAddress(), packet.getPort(), buf, packet.getLength());
                    }
                } catch (Exception e) {
                    logger.warn("Exception [{}] [{}]", e.getClass(), e.getMessage());
                    e.printStackTrace();
                    if (e.getClass() != IOException.class) {
                        isQuit = true;
                    }
                }
            }
            logger.info("UdpServer server ({}) end", localPort);
        }
    }

    class FfmpegRunnable implements Runnable {
        @Override
        public void run() {

            logger.info("Ffmpeg proc ({}) start", inputPipeName);

            if (inputCodec != null && inputCodec.equals(SurfMsgVocoder.VOCODER_ALAW)) {
                ShellUtil.startAlawTranscoding(inputPipeName, outputPipeName);
            }
            else {
                ShellUtil.startAMRTranscoding(inputPipeName, outputPipeName);
            }

            logger.info("Ffmpeg proc ({}) end", inputPipeName);
        }
    }

    private static final int LINEAR_PAYLOAD_SIZE = 320;

    class RmqRelayRunnable implements Runnable {
        @Override
        public void run() {
            logger.info("Rmq relay proc ({}) start", outputPipeName);

            byte[] pipeBuf = new byte[LINEAR_PAYLOAD_SIZE];
            try {
                logger.info("Rmq relay proc ({}) ready. isQuit [{}]", outputPipeName, isQuit);

                while (!isQuit) {
                    int size = outputPipeFile.read(pipeBuf);
                    if (size > 0 && rmqClient != null && rmqClient.isConnected()) {

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
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            logger.info("Rmq relay proc ({}) end", outputPipeName);
        }
    }
}
