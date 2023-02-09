package imitator.exchangeprotocol;

import imitator.common.exception.CriticalRuntimeException;
import imitator.message.East;
import imitator.message.Message;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * The data exchange protocol implements {@link imitator.exchangeprotocol.Protocol}
 */
public final class EastProtocol extends Protocol {

    EastProtocol(Charset inEncoding, Charset outEncoding) {
        super(inEncoding, outEncoding);
    }

    @Override
    public List<Message> unpack(ByteBuffer rawData) {
        List<ByteBuffer> buffers = split(rawData);

        List<Message> out = new ArrayList<>();
        for (ByteBuffer buf:
                buffers) {
            if(Thread.currentThread().isInterrupted()) {
                Thread.currentThread().interrupt();
                logger.info("interrupted");
                break;
            }
            out.add(newMessage(buf));
        }

        findDelay(out);

        return out;
    }

    private East newMessage(ByteBuffer buf) {

        int beginPosition = buf.position();

        // Begin bytes. Move offset
        buf.position(buf.position() + 2);

        // Size. Move offset
        buf.position(buf.position() + 4);

        // Time
        int time = buf.getInt() * 1000;

        // Object name
        byte[] name = new byte[6];
        buf.get(name);

        // The message is complete
        buf.position(beginPosition);
        byte[] all = new byte[buf.limit() - beginPosition];
        buf.get(all);

        return new East(time, new String(name), all);
    }

    @Override
    public byte[] pack(Message msg) {

        if(msg instanceof East) {
            return msg.getData();
        }
        return new byte[0];
    }

    private List<ByteBuffer> split(ByteBuffer buf) {
        if(!buf.isReadOnly())
            throw new CriticalRuntimeException("you have to use read-only buf in the protocol.split");

        List<ByteBuffer> list = new ArrayList<>();
        for (int i = 0; ;) {
            if(Thread.currentThread().isInterrupted()) {
                Thread.currentThread().interrupt();
                logger.info("interrupted");
                return list;
            }

            if (buf.get(i) == 0x62
                    && buf.get(i + 1) == 0x26) {
                buf.position(i + 2);

                int size = buf.getInt();

                if (buf.get(i + size - 2) == 0x43
                        && buf.get(i + size - 1) == 0x34) {
                    list.add(buf
                            .duplicate()
                            .position(i)
                            .limit(i + size)
                    );
                }
                i += size;
            } else {
                i++;
            }
            if(i >= buf.limit())
                break;
        }

        return list;
    }
}
