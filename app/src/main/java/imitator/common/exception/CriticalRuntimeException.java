package imitator.common.exception;

public class CriticalRuntimeException extends RuntimeException {

    public CriticalRuntimeException(String message) {
        super(message);
    }

    public CriticalRuntimeException() {
        super();
    }
}
