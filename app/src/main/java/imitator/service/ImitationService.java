package imitator.service;

import imitator.App;
import imitator.common.exception.CriticalRuntimeException;
import imitator.connector.Sendable;
import imitator.exchangeprotocol.ProtocolType;
import imitator.repository.Repository;

import java.util.List;
import java.util.logging.Logger;

public abstract class ImitationService {

    protected static final Logger logger = Logger.getLogger(App.class.getName());

    protected final List<Sendable> senders;

    protected boolean isExit = false;

    protected final String name;

    protected boolean isRepeat;

    protected ImitationService(List<Sendable> senders, String serviceName, boolean isRepeat) {
        this.senders = senders;
        this.name = serviceName;
        this.isRepeat = isRepeat;
    }

    public static class Builder {

        private boolean isRepeat = false;

        private Repository repository;

        public Builder() {}

        public ImitationService build(List<Sendable> senders, ProtocolType type) {

            if(senders == null || type == null)
                throw new CriticalRuntimeException("The ImitationService has not been created. Something is null.");

            switch (type) {
                case NORTH:
                    throw new CriticalRuntimeException("Not found implementation");
                case EAST:
                    return new EastImitationService(senders, repository, isRepeat);
            }

            throw new CriticalRuntimeException("ProtocolType not found");
        }

        public Builder setRepository(Repository repository) {
            this.repository = repository;
            return this;
        }

        public Builder setRepeat(boolean repeat) {
            isRepeat = repeat;
            return this;
        }
    }

    public void stop() {
        isExit = true;
    }

    public boolean isStarted() {
        return !isExit;
    }

    public String getName() {
        return name;
    }

    abstract public void send();
}
