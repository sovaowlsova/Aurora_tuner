package com.sovaowlsova.auroratuner.tuner.ui;

public class TunerUiState {
    private final String noteText;
    private final String deltaText;
    private final int noteColor;
    private final float lineBias;

    public TunerUiState(String noteText, String deltaText, int noteColor, float lineBias) {
        this.noteText = noteText;
        this.deltaText = deltaText;
        this.noteColor = noteColor;
        this.lineBias = lineBias;
    }

    public String getNoteText() {
        return noteText;
    }

    public String getDeltaText() {
        return deltaText;
    }

    public int getNoteColor() {
        return noteColor;
    }

    public float getLineBias() {
        return lineBias;
    }
}
