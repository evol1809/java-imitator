package imitator.common.validation;

import imitator.common.exception.CriticalRuntimeException;
import imitator.message.Message;

import java.util.List;

public class EastImitationServiceValidation {

    private static void notifyError(StringBuilder stringBuilder) {
        if(stringBuilder.length() == 0)
            return;

        throw new CriticalRuntimeException(
                stringBuilder.insert(0,
                        EastImitationServiceValidation.class.getSimpleName() + " ").toString());
    }
    public static void valid(List<Message> messages) {

        StringBuilder stringBuilder = new StringBuilder();

        if(messages == null) {
            stringBuilder.append("Messages must not be null. ");
            notifyError(stringBuilder);
        } else if(messages.size() == 0) {
            stringBuilder.append("Messages size must be greater than 0. ");
            notifyError(stringBuilder);
        }

        for(int i = 0; i < messages.size(); i++) {
            if(Thread.currentThread().isInterrupted()) {
                Thread.currentThread().interrupt();
                notifyError(stringBuilder);
            }
            if( messages.get(i).getData().length == 0) {
                stringBuilder.append("Raw data size must be greater than 0");
                break;
            }
        }
        notifyError(stringBuilder);
    }
}
