package imitator.common.validation;

import imitator.common.config.ImitatorConfig;
import imitator.common.config.connector.TcpServerConfig;
import imitator.common.exception.CriticalRuntimeException;
import imitator.exchangeprotocol.ProtocolType;

public class ImitatorConfigValidation {

    private static void notifyError(String msg) {
        throw new CriticalRuntimeException(ImitatorConfigValidation.class.getSimpleName() + ": " + msg);
    }

    public static void valid(ImitatorConfig config) {
        boolean isTcpServer = false;
        for(TcpServerConfig tcpServerConfig : config.getConnector().getTcpServerList()) {
            int port = tcpServerConfig.getPort();
            if (port <= 0 || port >= 65535)
                notifyError("port num is invalid");
            isTcpServer = true;
        }

        if(!isTcpServer)
            notifyError("must be a tcp server");

        if(!config.getProtocol().getType().equals(ProtocolType.EAST))
            notifyError("protocol type must be EAST");

        if(config.getRepository().getFile().getFilePath().isEmpty())
            notifyError("file path must be not empty");
    }
}
