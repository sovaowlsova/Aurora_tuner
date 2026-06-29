package com.sovaowlsova.auroratuner.core.data;

public enum BuiltInInstrumentId {
    GUITAR_6("Guitar6"),
    UKULELE("Ukulele");

    private final String id;

    private BuiltInInstrumentId(String id) {
        this.id = id;
    }

    public String get() {
        return id;
    }
}
