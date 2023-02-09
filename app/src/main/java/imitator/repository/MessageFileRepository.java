package imitator.repository;

import imitator.App;
import imitator.common.config.ProtocolConfig;
import imitator.common.config.repository.RepositoryConfig;
import imitator.common.utils.FileReader;
import imitator.exchangeprotocol.Protocol;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.logging.Logger;

public class MessageFileRepository<Message> implements Repository<Message> {
    private static final Logger logger = Logger.getLogger(App.class.getName());

    private Protocol protocol;

    private final String filePath;

    private List<imitator.message.Message> messages;

    public MessageFileRepository(RepositoryConfig repositoryConfig, ProtocolConfig protocolConfig) {
        this.filePath = repositoryConfig.getFile().getFilePath();
        protocol = Protocol.newProtocol(protocolConfig);
    }

    public void load() {
        logger.info("Preparing MessageFileRepository...");

        messages = protocol.unpack(
                ByteBuffer.wrap(FileReader.readAll(filePath))
                        .asReadOnlyBuffer()
        );
    }

    @Override
    public List<imitator.message.Message> getAll(){
        return messages;
    }
}
