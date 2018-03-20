package com.uangel.core.sdp;

import gov.nist.javax.sdp.SessionDescriptionImpl;
import gov.nist.javax.sdp.parser.SDPAnnounceParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sdp.*;
import java.util.Vector;

public class SdpParser {

    private static final Logger logger = LoggerFactory.getLogger(SdpParser.class);

    public SdpInfo parse(String msg) throws Exception {
        SDPAnnounceParser parser = new SDPAnnounceParser(msg);
        SessionDescriptionImpl sdp = parser.parse();

        Vector mdVector = sdp.getMediaDescriptions(false);
        if (mdVector == null) {
            return null;
        }

        MediaDescription md = (MediaDescription)mdVector.get(0);

        Connection connection = sdp.getConnection();
        if (connection == null) {
            connection = md.getConnection();
        }

        if (connection == null) {
            //
            // TODO
            //

            return null;
        }

        Media media = md.getMedia();
        if (media == null) {
            return null;
        }

        SdpInfo sdpInfo = new SdpInfo();
        sdpInfo.setRemoteIp(connection.getAddress());
        sdpInfo.setRemotePort(media.getMediaPort());

        Vector<Integer> mediaFormats = new Vector<>();

        for (Object obj: media.getMediaFormats(false)) {

            Integer payloadId = Integer.valueOf((String)obj);

            mediaFormats.add(payloadId);
            sdpInfo.addAttribute(payloadId, null);
        }

        for (Object obj: md.getAttributes(false)) {

            Attribute attr = (Attribute)obj;
            if (attr.getName() == null) {
                continue;
            }

            if (!attr.hasValue()) {
                sdpInfo.addAttribute(attr.getName(), null);
                continue;
            }

            String value = attr.getValue();

            if (attr.getName().equals("rtpmap")) {
                int space = value.indexOf(' ');
                if (space <= 0) {
                    continue;
                }

                Integer payloadId = Integer.valueOf(value.substring(0, space));
                String description = value.substring(space + 1);

                if (mediaFormats.contains(payloadId)) {
                    mediaFormats.remove(payloadId);
                }

                sdpInfo.updateAttribute(payloadId, description);
            }
            else {
                sdpInfo.addAttribute(attr.getName(), value);
            }
        }

        // sdp connection addr 1.255.239.173 type IP4 net IN
        logger.debug("sdp connection addr {} type {} net {}", sdpInfo.getRemoteIp(), connection.getAddressType(), connection.getNetworkType());

        // sdp media port 35664 type audio
        logger.debug("sdp media port {} type {}", sdpInfo.getRemotePort(), media.getMediaType());

        // sdp media [0, 8, 4, 18, 101]
        logger.debug("sdp media {}", media.getMediaFormats(false).toString());

        for (SdpAttribute attr: sdpInfo.getAttributes()) {
            // sdp attr name [rtpmap] payload [0] value [PCMU/8000]
            logger.debug("sdp attr name [{}] payload [{}] value [{}]", attr.getName(), attr.getPayloadId(), attr.getDescription());
        }

        return sdpInfo;
    }
}