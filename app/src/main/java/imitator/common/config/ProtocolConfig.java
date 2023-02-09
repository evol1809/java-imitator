package imitator.common.config;

import imitator.exchangeprotocol.ProtocolType;

import java.nio.charset.Charset;

public class ProtocolConfig {

    public ProtocolConfig() {
    }

    public ProtocolConfig(Charset outEncoding, Charset inEncoding, ProtocolType type) {
        this.outEncoding = outEncoding;
        this.inEncoding = inEncoding;
        this.type = type;
    }

    private Charset outEncoding = Charset.defaultCharset();

    private Charset inEncoding = Charset.defaultCharset();

    private ProtocolType type = ProtocolType.EAST;

    public Charset getOutEncoding() {
        return outEncoding;
    }

    public void setOutEncoding(Charset outEncoding) {
        this.outEncoding = outEncoding;
    }

    public Charset getInEncoding() {
        return inEncoding;
    }

    public void setInEncoding(Charset inEncoding) {
        this.inEncoding = inEncoding;
    }

    public ProtocolType getType() {
        return type;
    }

    public void setType(ProtocolType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ProtocolConfig{" +
                "outEncoding=" + outEncoding +
                ", inEncoding=" + inEncoding +
                ", type=" + type +
                '}';
    }
}
