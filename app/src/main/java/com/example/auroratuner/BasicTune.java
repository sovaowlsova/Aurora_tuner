package com.example.auroratuner;

public enum BasicTune {
    StandardGuitar(new Tune("Guitar", new Note[] {Note.E4, Note.B3, Note.G3, Note.D3, Note.A2, Note.E2})),
    DropDGuitar(new Tune("Guitar", new Note[] {Note.E4, Note.B3, Note.G3, Note.D3, Note.A2, Note.D2}));
    private final Tune tune;

    BasicTune(Tune tune) {
        this.tune = tune;
    }

    public Tune getTune() {
        return tune;
    }
}
