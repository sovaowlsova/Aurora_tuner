package com.sovaowlsova.auroratuner.core.model.Exceptions;

import java.io.IOException;

public class NoInternetException extends IOException {
    public NoInternetException() {
        super("No internet connection");
    }

    public NoInternetException(String message) {
        super(message);
    }

    public NoInternetException(String message, Throwable cause) {
        super(message, cause);
    }
}
