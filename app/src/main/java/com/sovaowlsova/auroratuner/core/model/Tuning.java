package com.sovaowlsova.auroratuner.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sovaowlsova.auroratuner.core.data.Note;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Tuning {
    private final String instrumentId;
    private final String tuningName;
    private List<Note> tuning;

    public Tuning(String instrumentId, String tuningName, Note[] tuning) {
        this.instrumentId = instrumentId;
        this.tuningName = tuningName;
        this.tuning = Arrays.asList(tuning.clone());
    }

    @JsonCreator
    private Tuning(
            @JsonProperty("instrumentId") String instrumentId,
            @JsonProperty("tuningName") String tuningName,
            @JsonProperty("tuning") List<String> tuning
    ) {
        this.instrumentId = instrumentId;
        this.tuningName = tuningName;
        this.tuning = new ArrayList<>();
        // for cycle instead of stream for better logging
        for (String noteName : tuning) {
            try {
                Note note = Note.nameToNote(noteName);
                this.tuning.add(note);
            } catch (IllegalArgumentException e) {
                // TODO: add warning for the user
                System.out.println("Couldn't find note " + noteName);
            }
        }
    }

    public String getTuningName() {
        return tuningName;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public List<Note> getTuning() {
        return Collections.unmodifiableList(tuning);
    }

    public Note getClosestString(double frequency) {
        double minDelta = Double.MAX_VALUE;
        Note closestString = null;

        for (Note string : tuning) {
            double delta = Math.abs(string.getDelta(frequency));
            if (delta < minDelta) {
                minDelta = delta;
                closestString = string;
            }
        }

        return closestString;
    }
}
