package com.example.auroratuner;

public enum Instrument {
    Guitar("Guitar", "Гитара");
    private final String nameEN;
    private final String nameRU;

    Instrument(String nameEN, String nameRU) {
        this.nameEN = nameEN;
        this.nameRU = nameRU;
    }

    public String getNameEN() {
        return nameEN;
    }

    public String getNameRU() {
        return nameRU;
    }
}
