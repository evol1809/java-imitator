package imitator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import imitator.common.config.ImitatorConfig;
import imitator.common.config.ImitatorList;
import imitator.common.config.ProtocolConfig;
import imitator.common.config.connector.ConnectorConfig;
import imitator.common.config.connector.TcpServerConfig;
import imitator.common.config.repository.FileRepositoryConfig;
import imitator.common.config.repository.RepositoryConfig;
import imitator.exchangeprotocol.ProtocolType;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class CreateConfig {

    public static void main(String[] args) throws IOException {
        setConfig();
    }

    public static void setConfig() throws IOException {
        List<ImitatorConfig> imitatorConfigs = new ArrayList<ImitatorConfig>();

        // East-1
        List<TcpServerConfig> tcpServerConfigs = new ArrayList<TcpServerConfig>();
        tcpServerConfigs.add(new TcpServerConfig(11234, "0.0.0.0", 0));

        ConnectorConfig connector = new ConnectorConfig(tcpServerConfigs);

        FileRepositoryConfig file = new FileRepositoryConfig("/Users/oleg/Downloads/java1611/java-imitator-new/untitled/src/out/production/untitled/testdata.binary");
        RepositoryConfig repository = new RepositoryConfig(file);

        ProtocolConfig protocol = new ProtocolConfig(Charset.forName("UTF-8"), Charset.forName("UTF-8"), ProtocolType.EAST);

        imitatorConfigs.add(new ImitatorConfig("East-1", connector, repository, protocol, 4000,
                "split", new Byte[]{Byte.valueOf("83"), Byte.valueOf("13")}, true));

        // North-1
        ProtocolConfig protocolNorth = new ProtocolConfig(Charset.forName("UTF-8"), Charset.forName("UTF-8"), ProtocolType.NORTH);
        imitatorConfigs.add(new ImitatorConfig("North-1", connector, repository,
                protocolNorth, null, "split", null, null));


// We want to save this Employee in a YAML file
        ImitatorList list = new ImitatorList(true, imitatorConfigs, new TcpServerConfig());

// ObjectMapper is instantiated just like before
        ObjectMapper om = new ObjectMapper(new YAMLFactory());

// We write the `employee` into `person2.yaml`
        om.writeValue(
                new File("config2.yaml"),
                list);
    }
}
