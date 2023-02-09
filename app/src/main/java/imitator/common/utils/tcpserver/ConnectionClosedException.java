package imitator.common.utils.tcpserver;

import java.io.IOException;

/**
 * Exception throw when the client closes the connection.
 */
class ConnectionClosedException extends IOException {

    ConnectionClosedException() {
        super("Connection closed");
    }

}