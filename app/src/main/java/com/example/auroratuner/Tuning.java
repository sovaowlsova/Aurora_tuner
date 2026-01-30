package com.example.auroratuner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Tuning {
    private final String instrumentName;
    private final String tuningName;
    private final List<Note> tuning;

    public Tuning(String instrumentName, String tuningName, Note[] tuning) {
        this.instrumentName = instrumentName;
        this.tuningName = tuningName;
        this.tuning = Arrays.asList(tuning.clone());
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public List<Note> getTuning() {
        return Collections.unmodifiableList(tuning);
    }

    public Note getClosestString(double frequency) {
        double minDelta = Double.MAX_VALUE;
        Note closestString = null;

        for (Note string : tuning) {
            double delta = Math.abs(string.getDelta(frequency));
            if (Math.abs(string.getDelta(frequency)) < minDelta) {
                minDelta = delta;
                closestString = string;
            }
        }

        return closestString;
    }
}
