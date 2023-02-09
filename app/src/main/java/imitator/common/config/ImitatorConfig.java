package imitator.common.config;

import imitator.common.config.connector.ConnectorConfig;
import imitator.common.config.repository.RepositoryConfig;

import java.util.Arrays;

public class ImitatorConfig {

    public ImitatorConfig() {}

    public ImitatorConfig(String name, ConnectorConfig connector, RepositoryConfig repository, ProtocolConfig protocol,
                          Integer sendingIntervalMillis, String splitExp, Byte[] splitHexExp, Boolean isRepeat) {
        this.name = name;
        this.connector = connector;
        this.repository = repository;
        this.protocol = protocol;
        this.sendingIntervalMillis = sendingIntervalMillis;
        this.splitExp = splitExp;
        this.splitHexExp = splitHexExp;
        this.isRepeat = isRepeat;
    }

    private String name;

    private ConnectorConfig connector = new ConnectorConfig();

    private RepositoryConfig repository = new RepositoryConfig();

    private ProtocolConfig protocol = new ProtocolConfig();

    private Integer sendingIntervalMillis;

    private String splitExp;

    private Byte[] splitHexExp;

    private Boolean isRepeat;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ConnectorConfig getConnector() {
        return connector;
    }

    public void setConnector(ConnectorConfig connector) {
        this.connector = connector;
    }

    public RepositoryConfig getRepository() {
        return repository;
    }

    public void setRepository(RepositoryConfig repository) {
        this.repository = repository;
    }

    public ProtocolConfig getProtocol() {
        return protocol;
    }

    public void setProtocol(ProtocolConfig protocol) {
        this.protocol = protocol;
    }

    public Integer getSendingIntervalMillis() {
        return sendingIntervalMillis;
    }

    public void setSendingIntervalMillis(Integer sendingIntervalMillis) {
        this.sendingIntervalMillis = sendingIntervalMillis;
    }

    public String getSplitExp() {
        return splitExp;
    }

    public void setSplitExp(String splitExp) {
        this.splitExp = splitExp;
    }

    public Byte[] getSplitHexExp() {
        return splitHexExp;
    }

    public void setSplitHexExp(Byte[] splitHexExp) {
        this.splitHexExp = splitHexExp;
    }

    public Boolean getRepeat() {
        return isRepeat;
    }

    public void setRepeat(Boolean repeat) {
        isRepeat = repeat;
    }

    @Override
    public String toString() {
        return "ImitatorConfig{" +
                "name='" + name + '\'' +
                ", connector=" + connector +
                ", repository=" + repository +
                ", protocol=" + protocol +
                ", sendingIntervalMillis=" + sendingIntervalMillis +
                ", splitExp='" + splitExp + '\'' +
                ", splitHexExp=" + Arrays.toString(splitHexExp) +
                ", isRepeat=" + isRepeat +
                '}';
    }
}
