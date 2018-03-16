package com.uangel.acs;

import com.uangel.acs.common.NetUtil;
import com.uangel.acs.config.AmfConfig;
import com.uangel.acs.rmqif.module.RmqServer;
import com.uangel.acs.simulator.UdpRelay;
import com.uangel.core.rabbitmq.transport.RmqSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App
{
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main( String[] args )
    {
        // Test Message
        String json = "{   \"header\": {\n" +
                "      \"type\": \"msfmp_inbound_set_offer_req\",\n" +
                "      \"sessionId\": \"773917-3193@1.255.239.167\",\n" +
                "      \"transactionId\": 7390813034292,\n" +
                "      \"msgFrom\": \"mcu1_mcud\",\n" +
                "      \"trxType\": 0,\n" +
                "      \"reasonCode\": 0\n" +
                "   },\n" +
                "   \"body\": {\n" +
                "      \"from_no\": \"07081773916\",\n" +
                "      \"to_no\": \"09933\",\n" +
                "      \"sdp\": \"v=0\\r\\no=- 7432311109587324954 4392946837496567243 IN IP4 1.255.239.170\\r\\ns=-\\r\\nt=0 0\\r\\nm=audio 35664 RTP/AVP 0 8 4 18 101\\r\\nc=IN IP4 1.255.239.173\\r\\na=rtpmap:0 PCMU/8000\\r\\na=rtpmap:8 PCMA/8000\\r\\na=rtpmap:4 G723/8000\\r\\na=rtpmap:18 G729/8000\\r\\na=ptime:20\\r\\na=rtpmap:101 telephone-event/8000\\r\\na=fmtp:101 0-16\\r\\na=sendrecv\\r\\na=direction:active\\r\\n\",\n" +
                "      \"outbound\": false\n" +
                "   }\n" +
                "}";

        AppInstance instance = AppInstance.getInstance();
        instance.setConfig(new AmfConfig());

        AmfConfig config = instance.getConfig();

        logger.info("Checking RMQ target [{}]", config.getRmqHost());
        boolean rmqAvailable = NetUtil.ping(config.getRmqHost(), 1000);
        logger.info("Host [{}] is {}", config.getRmqHost(), rmqAvailable ? "reachable" : "NOT reachable");


        if (!rmqAvailable) {
            logger.error("Process exited");
            return;
        }

        NetUtil.getLocalIP();

        UdpRelay udpRelay = new UdpRelay();
        udpRelay.openUdpServer(10034);

        RmqServer rmqServer = null;

        rmqServer = new RmqServer();
        rmqServer.start();

        try {

            // To test a message
            RmqSender sender = new RmqSender(config.getRmqHost(), config.getRmqUser(), config.getRmqPass(), config.getLocalName());
            sender.connect();
            sender.send("{   \"header\": {\n" +
                    "      \"type\": \"msfmp_inbound_set_offer_req\",\n" +
                    "      \"sessionId\": \"773917-3193@1.255.239.167\",\n" +
                    "      \"transactionId\": 7390813034292,\n" +
                    "      \"msgFrom\": \"mcu1_mcud\",\n" +
                    "      \"trxType\": 0,\n" +
                    "      \"reasonCode\": 0\n" +
                    "   },\n" +
                    "   \"body\": {\n" +
                    "      \"from_no\": \"07081773916\",\n" +
                    "      \"to_no\": \"09933\",\n" +
                    "      \"sdp\": \"v=0\\r\\no=- 7432311109587324954 4392946837496567243 IN IP4 1.255.239.170\\r\\ns=-\\r\\nt=0 0\\r\\nm=audio 35664 RTP/AVP 0 8 4 18 101\\r\\nc=IN IP4 1.255.239.173\\r\\na=rtpmap:0 PCMU/8000\\r\\na=rtpmap:8 PCMA/8000\\r\\na=rtpmap:4 G723/8000\\r\\na=rtpmap:18 G729/8000\\r\\na=ptime:20\\r\\na=rtpmap:101 telephone-event/8000\\r\\na=fmtp:101 0-16\\r\\na=sendrecv\\r\\na=direction:active\\r\\n\",\n" +
                    "      \"outbound\": false\n" +
                    "   }\n" +
                    "}");

            Thread.sleep(100);

            sender.send("{\n" +
                    "\"header\": {\n" +
                    "\"type\": \"msfmp_inbound_get_answer_req\",\n" +
                    "\"sessionId\": \"AAqzXgAACP4AABeyAf_sJw--c4655@xener.com\",\n" +
                    "\"transactionId\": 517234739004,\n" +
                    "\"msgFrom\": \"mcu1_mcud\",\n" +
                    "\"trxType\": 0,\n" +
                    "\"reasonCode\": 0\n" +
                    "}\n}");

            Thread.sleep(100);

            sender.send("{\n" +
                    "   \"header\": {\n" +
                    "      \"type\": \"msfmp_nego_done_req\",\n" +
                    "      \"sessionId\": \"AAqzXgAACP4AABeyAf_sJw--c4655@xener.com\",\n" +
                    "      \"transactionId\": 517234747124,\n" +
                    "      \"msgFrom\": \"mcu1_mcud\",\n" +
                    "      \"trxType\": 0,\n" +
                    "      \"reasonCode\": 0\n" +
                    "   },\n" +
                    "   \"body\": {}\n" +
                    "}");

            Thread.sleep(500);

            sender.send("{\n" +
                    "   \"header\": {\n" +
                    "      \"type\": \"msfmp_hangup_req\",\n" +
                    "      \"sessionId\": \"AAWFHQAABNkAABezAf_sJw--c4706@xener.com\",\n" +
                    "      \"transactionId\": 585819438444,\n" +
                    "      \"msgFrom\": \"mcu1_mcud\",\n" +
                    "      \"trxType\": 0,\n" +
                    "      \"reasonCode\": 0\n" +
                    "   }\n" +
                    "}\n");

            sender.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //
        // TODO
        //

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (rmqServer != null) {
            rmqServer.stop();
        }

        udpRelay.closeUdpServer();

        logger.info("Process End..");
    }
}
