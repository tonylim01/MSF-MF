package x3.player.mru.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x3.player.core.socket.UdpCallback;
import x3.player.core.socket.UdpSocket;
import x3.player.mru.AppInstance;
import x3.player.mru.rmqif.module.RmqClient;

import java.io.FileOutputStream;

public class BiUdpRelay {

    private static final Logger logger = LoggerFactory.getLogger(BiUdpRelay.class);

    /**
     * Working like below:
     *   srcLocalPort -> dstUdpSocket
     *   dstLocalPort -> srcUdpSocket
     */
    private UdpSocket srcUdpSocket = null;
    private UdpSocket dstUdpSocket = null;

    private int srcLocalPort;
    private int dstLocalPort;

    private AiifRelay aiifRelay = null;
    private String dstQueueName = null;

    private long audioDetectLevel = 0;
    private long silenceDetectLevel = 0;

    private boolean isEnergyDetected = false;

    public void setSrcLocalPort(int localPort) {
        srcLocalPort = localPort;
    }

    public void setDstLocalPort(int localPort) {
        dstLocalPort = localPort;
    }

    /**
     * Transports packets received on localPort to remoteIp:remotePort
     * @param remoteIpAddress
     * @param remotePort
     */
    public void openSrcUdpClient(String remoteIpAddress, int remotePort) {
        logger.debug("Open src UDP client. remote [{}:{}] lport [{}]", remoteIpAddress, remotePort, srcLocalPort);
        srcUdpSocket = new UdpSocket(remoteIpAddress, remotePort, srcLocalPort);
        srcUdpSocket.setUdpCallback(new RelayUdpCallback(srcUdpSocket));
        srcUdpSocket.start();

        updateRemoteSocket();
    }

    public void openDstUdpClient(String remoteIpAddress, int remotePort) {
        logger.debug("Open dst UDP client. remote [{}:{}] lport [{}]", remoteIpAddress, remotePort, dstLocalPort);
        dstUdpSocket = new UdpSocket(remoteIpAddress, remotePort, dstLocalPort);
        dstUdpSocket.setUdpCallback(new RelayUdpCallback(dstUdpSocket));
        dstUdpSocket.start();

        updateRemoteSocket();
    }

    private void updateRemoteSocket() {
        if (srcUdpSocket != null && dstUdpSocket != null) {
            dstUdpSocket.setRemoteSocket(srcUdpSocket);
        }
        if (srcUdpSocket != null && dstUdpSocket != null) {
            srcUdpSocket.setRemoteSocket(dstUdpSocket);
        }

    }

    public void setDupUdpQueue(String inputCodec, String dstQueueName) {
        logger.debug("Open UDP relay queue [{}]", dstQueueName);
        this.dstQueueName = dstQueueName;
//        if (dstUdpSocket != null) {
        if (srcUdpSocket != null) {
            audioDetectLevel = AppInstance.getInstance().getConfig().getAudioEnergyLevel();
            silenceDetectLevel = AppInstance.getInstance().getConfig().getSilenceEnergyLevel();

            aiifRelay = new AiifRelay();

            aiifRelay.setInputCodec(inputCodec);
            aiifRelay.setRelayQueue(dstQueueName);

            String filename = String.format("/tmp/%s.pcm", dstQueueName);
            aiifRelay.saveToFile(filename);
            aiifRelay.start();

            srcUdpSocket.setTag(aiifRelay.hashCode());
        }
    }

    public void closeUdpSocket() {
        if (srcUdpSocket != null) {
            srcUdpSocket.stop();
        }
        if (dstUdpSocket != null) {
            dstUdpSocket.stop();
        }

        try {
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (aiifRelay != null) {
            aiifRelay.stop();
        }
    }

    private long linearSum = 0;
    private int linearSumCount = 0;

    class RelayUdpCallback implements UdpCallback {

        private UdpSocket udpSocket;

        public RelayUdpCallback(UdpSocket udpSocket) {
            this.udpSocket = udpSocket;
        }

        @Override
        public void onReceived(byte[] srcAddress, int srcPort, byte[] buf, int length) {
            if (udpSocket.getRemoteSocket() != null) {
                udpSocket.getRemoteSocket().send(buf, length);
                if (udpSocket.getTag() != 0) {
                    aiifRelay.send(buf, length);

                    if (audioDetectLevel > 0 && silenceDetectLevel > 0) {
                        energyDetect(buf, length);
                    }
                }
            }
        }

        private boolean energyDetect(byte[] buf, int length) {
            if (buf == null || length < 12) {
                return false;
            }

            long sum = 0;
            for (int i = 12; i < length; i += 2) {
                short value = (short)((short)(((buf[i + 1] & 0xff) << 8) & 0xff00) | (short)(buf[i] & 0xff));
                if (value > 0) {
                    sum += value;
                }
            }

            linearSum += sum;
            linearSumCount++;
            if (linearSumCount >= 5) {

                logger.info("energy = {}", linearSum);
                if (linearSum >= audioDetectLevel) {
                    //
                    // TODO: Voice detected
                    //
                    logger.info("Energy Detected");

                    isEnergyDetected = true;
                }
                else if (isEnergyDetected && linearSum < silenceDetectLevel) {
                    //
                    // TODO: Silence detected
                    //
                    logger.info("Silence Detected");

                    isEnergyDetected = false;
                }

                linearSumCount = 0;
                linearSum = 0;
            }

            return true;
        }
    }
}
