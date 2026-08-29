package com.sovaowlsova.auroratuner.core.model.Exceptions;

public class InvalidTuningException extends IllegalArgumentException {
    public InvalidTuningException() {
        super("Invalid tuning");
    }

    public InvalidTuningException(String message) {
        super(message);
    }

    public InvalidTuningException(String message, Throwable cause) {
        super(message, cause);
    }
}
