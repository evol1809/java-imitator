package imitator.connector;

import imitator.common.config.connector.TcpServerConfig;
import imitator.common.utils.tcpserver.TcpServer;
import imitator.threadfactory.UncaughtExceptionHandler;

import java.util.concurrent.ThreadFactory;

public class Connector implements Sendable {

    private TcpServer tcpServer;

    public Connector(TcpServerConfig config
            , ThreadFactory threadFactory, UncaughtExceptionHandler uncaughtExceptionHandler) {
        tcpServer = new TcpServer(config.getPort(), threadFactory, uncaughtExceptionHandler);
    }

    public void start() {
        tcpServer.start();
    }

    @Override
    public void send(byte[] msg) {
        tcpServer.send(msg);
    }

    public TcpServer getTcpServer() {
        return tcpServer;
    }
}
