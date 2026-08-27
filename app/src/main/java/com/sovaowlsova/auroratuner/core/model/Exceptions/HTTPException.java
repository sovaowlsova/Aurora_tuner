package com.sovaowlsova.auroratuner.core.model.Exceptions;

public class HTTPException extends NoInternetException {
    public int code;

    public HTTPException(int code) {
        this.code = code;
    }
}
