package x3.player.mru.surfif.messages;

import com.google.gson.annotations.SerializedName;

public class SurfMsgAgc {

    @SerializedName("encoder_side")
    private SurfMsgAgcData encoderSide;
    @SerializedName("decoder_side")
    private SurfMsgAgcData decoderSide;

    public void setEncoderSide(SurfMsgAgcData encoderSide) {
        this.encoderSide = encoderSide;
    }

    public void setDecoderSide(SurfMsgAgcData decoderSide) {
        this.decoderSide = decoderSide;
    }
}
