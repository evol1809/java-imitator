package imitator.exchangeprotocol;

import imitator.message.Message;

import java.nio.ByteBuffer;
import java.util.List;

public interface Unpack {

    List<Message> unpack(ByteBuffer rawData);
}
