package com.sovaowlsova.auroratuner.tuner.ui;

public class TunerUiState {
    private final String noteText;
    private final int noteColor;
    private final float lineBias;

    public TunerUiState(String noteText, int noteColor, float lineBias) {
        this.noteText = noteText;
        this.noteColor = noteColor;
        this.lineBias = lineBias;
    }

    public String getNoteText() {
        return noteText;
    }

    public int getNoteColor() {
        return noteColor;
    }

    public float getLineBias() {
        return lineBias;
    }
}
