package x3.player.mru.surfif.messages;

import java.util.ArrayList;
import java.util.List;

public class SurfMsgSetConfigStatus {

    private List<Status> status;

    public SurfMsgSetConfigStatus(int size) {
        if (size <= 0) {
            size = 1;
        }
        status = new ArrayList<>(size);
    }

    public void add(String type, int period) {
        Status item = new Status();
        item.setType(type);
        item.setPeriod(period);

        status.add(item);
    }

    private class Status {
        private String type;
        private int period;

        public void setType(String type) {
            this.type = type;
        }

        public void setPeriod(int period) {
            this.period = period;
        }
    }
}
