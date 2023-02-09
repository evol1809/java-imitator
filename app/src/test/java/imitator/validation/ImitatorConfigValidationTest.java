package imitator.validation;

import imitator.common.config.ImitatorConfig;
import imitator.common.config.ProtocolConfig;
import imitator.common.config.connector.ConnectorConfig;
import imitator.common.config.connector.TcpServerConfig;
import imitator.common.config.repository.FileRepositoryConfig;
import imitator.common.config.repository.RepositoryConfig;
import imitator.common.exception.CriticalRuntimeException;
import imitator.common.validation.ImitatorConfigValidation;
import imitator.exchangeprotocol.ProtocolType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ImitatorConfigValidationTest {

    @Test
    @DisplayName("port num is invalid")
    void portNumIsInvalid() {
        String exceptionMessage = "";
        try {
            ImitatorConfig imitatorConfig = new ImitatorConfig();

            TcpServerConfig tcpServerConfig = new TcpServerConfig();
            tcpServerConfig.setPort(0);

            ConnectorConfig connectorConfig =
                    new ConnectorConfig(Stream.of(tcpServerConfig).collect(Collectors.toList()));

            imitatorConfig.setConnector(connectorConfig);

            ImitatorConfigValidation.valid(imitatorConfig);
        }catch (CriticalRuntimeException ex) {
            exceptionMessage = ex.getMessage();
        }

        assertEquals(ImitatorConfigValidation.class.getSimpleName()
                + ": port num is invalid", exceptionMessage);
    }

    @Test
    @DisplayName("must be a tcp server")
    void tcpServersExist() {
        String exceptionMessage = "";
        try {
            ImitatorConfig imitatorConfig = new ImitatorConfig();

            ConnectorConfig connectorConfig = new ConnectorConfig();

            imitatorConfig.setConnector(connectorConfig);

            ImitatorConfigValidation.valid(imitatorConfig);
        }catch (CriticalRuntimeException ex) {
            exceptionMessage = ex.getMessage();
        }

        assertEquals(ImitatorConfigValidation.class.getSimpleName()
                + ": must be a tcp server", exceptionMessage);
    }

    @Test
    @DisplayName("protocol type must be EAST")
    void protocolTypeIsEAST() {
        String exceptionMessage = "";
        try {
            ImitatorConfig imitatorConfig = new ImitatorConfig();

            TcpServerConfig tcpServerConfig = new TcpServerConfig();
            tcpServerConfig.setPort(12341);

            ConnectorConfig connectorConfig =
                    new ConnectorConfig(Stream.of(tcpServerConfig).collect(Collectors.toList()));

            imitatorConfig.setConnector(connectorConfig);

            ProtocolConfig protocolConfig = new ProtocolConfig();
            protocolConfig.setType(ProtocolType.NORTH);

            imitatorConfig.setProtocol(protocolConfig);

            ImitatorConfigValidation.valid(imitatorConfig);
        }catch (CriticalRuntimeException ex) {
            exceptionMessage = ex.getMessage();
        }

        assertEquals(ImitatorConfigValidation.class.getSimpleName()
                + ": protocol type must be EAST", exceptionMessage);
    }

    @Test
    @DisplayName("file path must be not empty")
    void filePathIsEmpty() {
        String exceptionMessage = "";
        try {
            ImitatorConfig imitatorConfig = new ImitatorConfig();

            TcpServerConfig tcpServerConfig = new TcpServerConfig();
            tcpServerConfig.setPort(12341);

            ConnectorConfig connectorConfig =
                    new ConnectorConfig(Stream.of(tcpServerConfig).collect(Collectors.toList()));

            imitatorConfig.setConnector(connectorConfig);

            ProtocolConfig protocolConfig = new ProtocolConfig();
            protocolConfig.setType(ProtocolType.EAST);

            imitatorConfig.setProtocol(protocolConfig);

            ImitatorConfigValidation.valid(imitatorConfig);
        }catch (CriticalRuntimeException ex) {
            exceptionMessage = ex.getMessage();
        }

        assertEquals(ImitatorConfigValidation.class.getSimpleName()
                + ": file path must be not empty", exceptionMessage);
    }

    @Test
    @DisplayName("file path must be not empty")
    void isGreat() {
        String exceptionMessage = "";
        try {
            ImitatorConfig imitatorConfig = new ImitatorConfig();

            TcpServerConfig tcpServerConfig = new TcpServerConfig();
            tcpServerConfig.setPort(12341);

            ConnectorConfig connectorConfig =
                    new ConnectorConfig(Stream.of(tcpServerConfig).collect(Collectors.toList()));

            imitatorConfig.setConnector(connectorConfig);

            ProtocolConfig protocolConfig = new ProtocolConfig();
            protocolConfig.setType(ProtocolType.EAST);

            imitatorConfig.setProtocol(protocolConfig);

            FileRepositoryConfig fileRepositoryConfig = new FileRepositoryConfig();
            fileRepositoryConfig.setFilePath("filepath");

            RepositoryConfig repositoryConfig = new RepositoryConfig();
            repositoryConfig.setFile(fileRepositoryConfig);

            imitatorConfig.setRepository(repositoryConfig);

            ImitatorConfigValidation.valid(imitatorConfig);
        }catch (CriticalRuntimeException ex) {
            exceptionMessage = ex.getMessage();
        }

        assertEquals("", exceptionMessage);
    }
}
