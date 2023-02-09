package imitator.message;

import static org.apache.commons.codec.binary.Hex.encodeHex;

public class East extends Message {

    private String description;

    public East(long time, String description, byte[] data) {
        super(time, data);
        this.description = description;
    }

    public String getDescription() { return description; }

    @Override
    public String toString() {
        return super.toString() + " " + description + " " + encodeHex(getData());
    }
}
