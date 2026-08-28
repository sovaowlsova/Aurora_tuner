package com.sovaowlsova.auroratuner.core.model.Exceptions;

import java.io.IOException;

public class HTTPException extends IOException {
    private final int code;

    public HTTPException(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
