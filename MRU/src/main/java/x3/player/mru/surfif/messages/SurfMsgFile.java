package x3.player.mru.surfif.messages;

public class SurfMsgFile {
    private String name;
    private Float duration;
    private String format;
    private Integer segment;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getSegment() {
        return segment;
    }

    public void setSegment(int segment) {
        this.segment = segment;
    }

}
