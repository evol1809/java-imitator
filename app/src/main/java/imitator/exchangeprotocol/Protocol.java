package imitator.exchangeprotocol;

import imitator.App;
import imitator.application.Imitator;
import imitator.common.config.ProtocolConfig;
import imitator.common.exception.CriticalRuntimeException;
import imitator.message.Message;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.List;
import java.util.logging.Logger;

/**
 * A {@link Imitator} data exchange protocol is an established set of rules
 * that determine how data is serializing and deserializing (parsing) from raw data.
 */
public abstract class Protocol  implements Pack, Unpack {

    protected static final Logger logger = Logger.getLogger(App.class.getName());

    CharsetDecoder decoder;

    CharsetEncoder encoder;

    Protocol(Charset inEncoding, Charset outEncoding) {
        decoder = outEncoding.newDecoder();
        encoder = inEncoding.newEncoder();
    }

    public static Protocol newProtocol(ProtocolConfig config){
        switch (config.getType()) {
            case EAST:
                return new EastProtocol(config.getInEncoding(), config.getOutEncoding());
        }
        throw new CriticalRuntimeException("ProtocolType not found");
    }

    protected void findDelay(List<Message> messages) {

        for (int i = 1; i < messages.size(); i++) {
            if(Thread.currentThread().isInterrupted()) {
                Thread.currentThread().interrupt();
                logger.info("interrupted");
                break;
            }
            Message message = messages.get(i);
            Message previousMessage = messages.get(i - 1);
            message.setDelayMillis(message.getTimeMillis() - previousMessage.getTimeMillis());
        }
    }
}
