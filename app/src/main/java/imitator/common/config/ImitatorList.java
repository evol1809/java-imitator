package imitator.common.config;

import imitator.common.config.connector.TcpServerConfig;

import java.util.List;

public class ImitatorList {

    public ImitatorList() {}

    public ImitatorList(Boolean isShell, List<ImitatorConfig> imitators, TcpServerConfig defaultTcpServer) {
        this.isShell = isShell;
        this.imitators = imitators;
        this.defaultTcpServer = defaultTcpServer;
    }

    private Boolean isShell = false;

    private List<ImitatorConfig> imitators;

    private TcpServerConfig defaultTcpServer =
            new TcpServerConfig(19474, "0.0.0.0", 4);

    public Boolean getShell() {
        return isShell;
    }

    public void setShell(Boolean shell) {
        isShell = shell;
    }

    public List<ImitatorConfig> getImitators() {
        return imitators;
    }

    public void setImitators(List<ImitatorConfig> imitators) {
        this.imitators = imitators;
    }

    public TcpServerConfig getDefaultTcpServer() {
        return defaultTcpServer;
    }

    public void setDefaultTcpServer(TcpServerConfig defaultTcpServer) {
        this.defaultTcpServer = defaultTcpServer;
    }

    @Override
    public String toString() {
        return "ImitatorList{" +
                "isShell=" + isShell +
                ", imitators=" + imitators +
                ", defaultTcpServer=" + defaultTcpServer +
                '}';
    }
}
