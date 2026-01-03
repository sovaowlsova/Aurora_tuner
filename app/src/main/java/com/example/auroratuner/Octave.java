package com.example.auroratuner;

public enum Octave {
    Zero(Note.C0.getFrequency(), Note.B0.getFrequency(), new Note[] {
            Note.C0, Note.C0sharp, Note.D0, Note.D0sharp, Note.E0, Note.F0, Note.F0sharp, Note.G0, Note.G0sharp, Note.A0, Note.A0sharp, Note.B0, Note.C1
    }),
    First(Note.B0.getFrequency(), Note.B1.getFrequency(), new Note[] {
            Note.B0, Note.C1, Note.C1sharp, Note.D1, Note.D1sharp, Note.E1, Note.F1, Note.F1sharp, Note.G1, Note.G1sharp, Note.A1, Note.A1sharp, Note.B1, Note.C2
    }),
    Second(Note.B1.getFrequency(), Note.B2.getFrequency(), new Note[] {
            Note.B1, Note.C2, Note.C2sharp, Note.D2, Note.D2sharp, Note.E2, Note.F2, Note.F2sharp, Note.G2, Note.G2sharp, Note.A2, Note.A2sharp, Note.B2, Note.C3
    }),
    Third(Note.B2.getFrequency(), Note.B3.getFrequency(), new Note[] {
            Note.B2, Note.C3, Note.C3sharp, Note.D3, Note.D3sharp, Note.E3, Note.F3, Note.F3sharp, Note.G3, Note.G3sharp, Note.A3, Note.A3sharp, Note.B3, Note.C4
    }),
    Fourth(Note.B3.getFrequency(), Note.B4.getFrequency(), new Note[] {
            Note.B3, Note.C4, Note.C4sharp, Note.D4, Note.D4sharp, Note.E4, Note.F4, Note.F4sharp, Note.G4, Note.G4sharp, Note.A4, Note.A4sharp, Note.B4, Note.C5
    }),
    Fifth(Note.B4.getFrequency(), Note.B5.getFrequency(), new Note[] {
            Note.B4, Note.C5, Note.C5sharp, Note.D5, Note.D5sharp, Note.E5, Note.F5, Note.F5sharp, Note.G5, Note.G5sharp, Note.A5, Note.A5sharp, Note.B5, Note.C6
    }),
    Sixth(Note.B5.getFrequency(), Note.B6.getFrequency(), new Note[] {
            Note.B5, Note.C6, Note.C6sharp, Note.D6, Note.D6sharp, Note.E6, Note.F6, Note.F6sharp, Note.G6, Note.G6sharp, Note.A6, Note.A6sharp, Note.B6, Note.C7
    }),
    Seventh(Note.B6.getFrequency(), Note.B7.getFrequency(), new Note[] {
            Note.B6, Note.C7, Note.C7sharp, Note.D7, Note.D7sharp, Note.E7, Note.F7, Note.F7sharp, Note.G7, Note.G7sharp, Note.A7, Note.A7sharp, Note.B7, Note.C8
    }),
    Eighth(Note.B7.getFrequency(), Note.B8.getFrequency(), new Note[] {
            Note.B7, Note.C8, Note.C8sharp, Note.D8, Note.D8sharp, Note.E8, Note.F8, Note.F8sharp, Note.G8, Note.G8sharp, Note.A8, Note.A8sharp, Note.B8
    });
    private final double lowestFrequency;
    private final double highestFrequency;
    private final Note[] notes;


    Octave(double lowestFrequency, double highestFrequency, Note[] notes) {
        this.lowestFrequency = lowestFrequency;
        this.highestFrequency = highestFrequency;
        this.notes = notes;
    }

    public double getLowestFrequency() {
        return lowestFrequency;
    }

    public double getHighestFrequency() {
        return highestFrequency;
    }

    public Note[] getNotes() {
        return notes.clone();
    }

    public Note getClosestNote(double frequency) {
        Note foundNote = null;

        double closestMatch = highestFrequency;
        for (Note note : notes) {
            double delta = Math.abs(note.getDelta(frequency));
            if (delta < closestMatch) {
                foundNote = note;
                closestMatch = delta;
            }
        }

        return foundNote;
    }
}
