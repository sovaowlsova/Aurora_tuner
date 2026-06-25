package com.sovaowlsova.auroratuner.core.model;

import com.sovaowlsova.auroratuner.core.data.Note;

public enum BuiltInTunings {
    Guitar6Standard(new Tuning("Guitar6", "Standard",
            new Note[] {Note.E4, Note.B3, Note.G3, Note.D3, Note.A2, Note.E2})),
    Guitar6DropD(new Tuning("Guitar6", "Drop D",
            new Note[] {Note.E4, Note.B3, Note.G3, Note.D3, Note.A2, Note.D2})),
    UkuleleStandard(new Tuning("Ukulele", "Standard",
            new Note[] {Note.A4, Note.E4, Note.G4, Note.C4}));
    private final Tuning tuning;

    BuiltInTunings(Tuning tuning) {
        this.tuning = tuning;
    }

    public Tuning get() {
        return tuning;
    }
}
