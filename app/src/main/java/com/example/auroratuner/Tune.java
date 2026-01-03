package com.example.auroratuner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Tune {
    private final String instrumentName;
    private final List<Note> strings;

    public Tune(String instrumentName, Note[] notes) {
        this.instrumentName = instrumentName;
        this.strings = Arrays.asList(notes.clone());
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public List<Note> getStrings() {
        return Collections.unmodifiableList(strings);
    }

    public Note getClosestString(double frequency) {
        double minDelta = Double.MAX_VALUE;
        Note closestString = null;

        for (Note string : strings) {
            double delta = Math.abs(string.getDelta(frequency));
            if (Math.abs(string.getDelta(frequency)) < minDelta) {
                minDelta = delta;
                closestString = string;
            }
        }

        return closestString;
    }
}
