package x3.player.core.sdp;

import x3.player.mru.surfif.messages.SurfMsgVocoder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SdpUtil {

    private static final Pattern rtpmapPattern = Pattern.compile("rtpmap:(\\d+) (.+)");

    /**
     * Parses "a=" attribute string
     * @param attributeStr
     * @return
     */
    public static SdpAttribute parseAttribute(String attributeStr) {
        if (attributeStr == null) {
            return null;
        }

        SdpAttribute attribute = new SdpAttribute();
        Matcher m = rtpmapPattern.matcher(attributeStr);
        if (m.find() && m.groupCount() >= 2) {
            attribute.setPayloadId(Integer.valueOf(m.group(1)));
            attribute.setDescription(m.group(2));
        }
        else {
            attribute.setDescription(attributeStr);
        }

        return attribute;
    }

    /**
     * Generates an attribute string which following after "a=" line
     * The result doesn't include "a="
     * @return
     */
    public static String getAttributeString(SdpAttribute attr) {
        StringBuilder sb = new StringBuilder(48);
        if (attr.getName() != null) {
            sb.append(attr.getName());
        }

        if (attr.getPayloadId() != SdpAttribute.PAYLOADID_NONE) {
            sb.append(":");
            sb.append(attr.getPayloadId());
            sb.append(" ");
        }
        else if (attr.getName().equals(SdpAttribute.NAME_FMTP) && attr.getDescription() != null) {
            sb.append(":");
        }

        if (attr.getDescription() != null) {
            sb.append(attr.getDescription());
        }

        return sb.toString();
    }

    public static String getCodecStr(int payloadType) {
        String codec = null;

        switch (payloadType) {
            case 0:
                codec = SurfMsgVocoder.VOCODER_ULAW;
                break;
            case 8:
                codec = SurfMsgVocoder.VOCODER_ALAW;
                break;
            case 9:
                codec = SurfMsgVocoder.VOCODER_G722;
                break;
        }

        return codec;
    }

    public static String getCodecStr(String sdpCodec) {
        if (sdpCodec == null) {
            return null;
        }

        String codec = null;

        if (sdpCodec.equals("AMR-WB"))
                codec = SurfMsgVocoder.VOCODER_AMR_WB;

        return codec;
    }
}
