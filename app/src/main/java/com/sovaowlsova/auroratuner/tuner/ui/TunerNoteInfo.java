package com.sovaowlsova.auroratuner.tuner.ui;

import com.sovaowlsova.auroratuner.core.data.Note;

public class TunerNoteInfo {
    private final Note note;
    private final Boolean inTune;
    private final double frequency;

    public TunerNoteInfo(Note note, Boolean inTune, double frequency) {
        this.note = note;
        this.inTune = inTune;
        this.frequency = frequency;
    }

    public Note getNote() {
        return note;
    }

    public Boolean getInTune() {
        return inTune;
    }

    public double getFrequency() {
        return frequency;
    }
}
