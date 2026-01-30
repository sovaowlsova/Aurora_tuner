package com.example.auroratuner;

public enum BuiltInTunings {
    Guitar6Standard(new Tuning("guitar6", "standard",
            new Note[] {Note.E4, Note.B3, Note.G3, Note.D3, Note.A2, Note.E2})),
    Guitar6DropD(new Tuning("guitar6", "dropD",
            new Note[] {Note.E4, Note.B3, Note.G3, Note.D3, Note.A2, Note.D2})),
    UkuleleStandard(new Tuning("ukulele", "standard",
            new Note[] {Note.A4, Note.E4, Note.G4, Note.C4}));
    private final Tuning tuning;

    BuiltInTunings(Tuning tuning) {
        this.tuning = tuning;
    }

    public Tuning get() {
        return tuning;
    }
}
