package com.example.auroratuner;

import android.content.Context;

import java.util.List;

public class JsonInstrument extends Instrument {
    private final String name;

    public JsonInstrument(String id, String name) {
        super(id);
        this.name = name;
    }

    public JsonInstrument(String id, String name, List<Tuning> tunings) {
        super(id, tunings);
        this.name = name;
    }

    @Override
    public String getName(Context context) {
        return name;
    }
}
