package com.example.auroratuner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Instrument {
    private final String id;
    private final String name;
    List<Tuning> tunings;


    public Instrument(String id, String name) {
        this.id = id;
        this.name = name;
        tunings = new ArrayList<>();
    }

    public Instrument(String id, String name, List<Tuning> tunings) {
        this.id = id;
        this.name = name;
        this.tunings = List.copyOf(tunings);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void addTuning(Tuning tuning) {
        tunings.add(tuning);
    }

    public List<Tuning> getTunings() {
        return Collections.unmodifiableList(tunings);
    }
}
