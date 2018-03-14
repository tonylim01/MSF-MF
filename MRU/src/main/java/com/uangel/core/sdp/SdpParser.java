package com.uangel.core.sdp;

import gov.nist.javax.sdp.SessionDescriptionImpl;
import gov.nist.javax.sdp.parser.SDPAnnounceParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sdp.*;
import java.util.Vector;

public class SdpParser {

    private static final Logger logger = LoggerFactory.getLogger(SdpParser.class);

    public boolean parse(String msg) throws Exception {
        SDPAnnounceParser parser = new SDPAnnounceParser(msg);
        SessionDescriptionImpl sdp = parser.parse();

        Vector mdVector = sdp.getMediaDescriptions(false);
        if (mdVector == null) {
            return false;
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

            return false;
        }

        // sdp connection addr 1.255.239.173 type IP4 net IN
        logger.debug("sdp connection addr {} type {} net {}", connection.getAddress(), connection.getAddressType(), connection.getNetworkType());

        Media media = md.getMedia();
        if (media == null) {
            return false;
        }

        // sdp media port 35664 type audio
        logger.debug("sdp media port {} type {}", media.getMediaPort(), media.getMediaType());

        // sdp media [0, 8, 4, 18, 101]
        logger.debug("sdp media {}", media.getMediaFormats(false).toString());

        for (Object obj: md.getAttributes(false)) {
            Attribute attr = (Attribute)obj;

            // sdp attr name rtpmap value 0 PCMU/8000
            logger.debug("sdp attr name {} value {}", attr.getName(), attr.hasValue() ? attr.getValue() : "-");
        }

        return true;
    }
}
