package x3.player.mcu.mru;

/**
 * Created by hwaseob on 2018-03-09.
 */
public class ClientException extends Exception {
    private Integer reasonCode;

    public ClientException(Integer reasonCode) {
        this.reasonCode = reasonCode;
    }

    public Integer getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(Integer reasonCode) {
        this.reasonCode = reasonCode;
    }
}
