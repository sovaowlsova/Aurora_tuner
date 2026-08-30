package com.sovaowlsova.auroratuner.core.model;

import android.content.Context;

import java.util.List;

public class JsonInstrument extends Instrument {
    private final String name;

    public JsonInstrument(String id, String name, int stringCount) {
        super(id, stringCount);
        this.name = name;
    }

    public JsonInstrument(String id, String name, int stringCount,  List<Tuning> tunings) {
        super(id, stringCount, tunings);
        this.name = name;
    }

    @Override
    public String getName(Context context) {
        return name;
    }
}
