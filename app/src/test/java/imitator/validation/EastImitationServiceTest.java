package imitator.validation;

import imitator.common.exception.CriticalRuntimeException;
import imitator.common.validation.EastImitationServiceValidation;
import imitator.message.East;
import imitator.message.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
public class EastImitationServiceTest {

    @Test
    @DisplayName("Raw data size must be greater than 0")
    void messageIsEmpty() {
        String exMessage = "";
        try {
            List<Message> messages = new ArrayList<>();
            messages.add(new East(1, "test", new byte[0]));
            EastImitationServiceValidation.valid(messages);
        } catch (CriticalRuntimeException ex) {
            exMessage = ex.getMessage();
        }
        assertEquals(EastImitationServiceValidation.class.getSimpleName()
                + " Raw data size must be greater than 0", exMessage);
    }

    @Test
    @DisplayName("Messages must not be null")
    void messageIsNull() {
        String exMessage = "";
        try {
            EastImitationServiceValidation.valid(null);
        } catch (CriticalRuntimeException ex) {
            exMessage = ex.getMessage();
        }
        assertEquals(EastImitationServiceValidation.class.getSimpleName()
                + " Messages must not be null. ", exMessage);
    }

    @Test
    @DisplayName("Messages size must be greater than 0")
    void messageSizeIsEmpty() {
        String exMessage = "";
        try {
            EastImitationServiceValidation.valid(new ArrayList<>());
        } catch (CriticalRuntimeException ex) {
            exMessage = ex.getMessage();
        }
        assertEquals(EastImitationServiceValidation.class.getSimpleName()
                + " Messages size must be greater than 0. ", exMessage);
    }


}