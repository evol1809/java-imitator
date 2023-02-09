package imitator.exchangeprotocol;

import imitator.message.Message;

public interface Pack {
    byte[] pack(Message msg);
}
