package imitator.message;

public abstract class Message {


    private long timeMillis;

    private long delayMillis;

    private byte[] data;

    public Message(long timeMillis, byte[] data) {
        this.timeMillis = timeMillis;
        this.data = data;
    }

    public long getTimeMillis() {
        return timeMillis;
    }

    public void setDelayMillis(long delayMillis) { this.delayMillis = delayMillis; }

    public long getDelayMillis() { return this.delayMillis; }

    public byte[] getData() { return data; }

    @Override
    public String toString() {
        return String.valueOf(timeMillis) + " " + String.valueOf(delayMillis);
    }
}
