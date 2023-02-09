package imitator.common.config.connector;

import java.util.ArrayList;
import java.util.List;

public class ConnectorConfig {
    public ConnectorConfig() {
    }

    public ConnectorConfig(List<TcpServerConfig> tcpServerList) {
        this.tcpServerList = tcpServerList;
    }

    private List<TcpServerConfig> tcpServerList = new ArrayList<>();


    public List<TcpServerConfig> getTcpServerList() {
        return tcpServerList;
    }

    public void setTcpServerList(List<TcpServerConfig> tcpServerList) {
        this.tcpServerList = tcpServerList;
    }



    @Override
    public String toString() {
        return "ConnectorList{" +
                "tcpServerList=" + tcpServerList +
                '}';
    }
}
