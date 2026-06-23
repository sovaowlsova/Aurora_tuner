package com.example.auroratuner;

import android.content.Context;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class Instrument {
    private final String id;
    private final List<Tuning> tunings;


    public Instrument(String id) {
        this.id = id;
        tunings = new ArrayList<>();
    }

    public Instrument(String id, List<Tuning> tunings) {
        this.id = id;
        this.tunings = List.copyOf(tunings);
    }

    public String getId() {
        return id;
    }

    public void addTuning(Tuning tuning) {
        tunings.add(tuning);
    }

    public List<Tuning> getTunings() {
        return new ArrayList<>(tunings);
    }

    public abstract String getName(Context context);

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Instrument that)) {
            return false;
        }
        return id.equals(that.id);
    }
}
