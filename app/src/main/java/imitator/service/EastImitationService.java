package imitator.service;


import imitator.common.validation.EastImitationServiceValidation;
import imitator.connector.Sendable;
import imitator.message.Message;
import imitator.repository.Repository;

import java.util.List;

final public class EastImitationService extends ImitationService {

    protected Repository repository;

    protected EastImitationService(List<Sendable> senders, Repository repository,
                                   boolean isRepeat) {
        super(senders, "EastImitationService", isRepeat);
        this.repository = repository;
    }

    @Override
    public void send() {
        List<Message> messages = repository.getAll();
        EastImitationServiceValidation.valid(messages);

        int i = 0;
        while (true) {
            if (Thread.currentThread().isInterrupted() || isExit) {
                Thread.currentThread().interrupt();
                break;
            }
            try {
                for(Sendable sender : senders) {
                    sender.send(messages.get(i).getData());
                }

                Thread.sleep(messages.get(i).getDelayMillis());
            } catch (InterruptedException e) {
                isExit = true;
                Thread.currentThread().interrupt();
            }
            i += 1;
            if(isRepeat) {
                if (i >= messages.toArray().length)
                    i = 0;
            } else {
                logger.info("The EastImitationService is finished");
                break;
            }
        }
    }
}
