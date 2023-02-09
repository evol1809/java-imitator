package imitator.common.config.connector;

public class TcpServerConfig {

    public TcpServerConfig() {}

    public TcpServerConfig(int port, String hostName, int connectingTimeoutMillis) {
        this.port = port;
        this.hostName = hostName;
        this.connectingTimeoutMillis = connectingTimeoutMillis;
    }

    private int port;

    private String hostName;

    private int connectingTimeoutMillis;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getConnectingTimeoutMillis() {
        return connectingTimeoutMillis;
    }

    public void setConnectingTimeoutMillis(int connectingTimeoutMillis) {
        this.connectingTimeoutMillis = connectingTimeoutMillis;
    }

    @Override
    public String toString() {
        return "TcpServerConfig{" +
                "port=" + port +
                ", hostName='" + hostName + '\'' +
                ", connectingTimeout=" + connectingTimeoutMillis +
                '}';
    }
}
