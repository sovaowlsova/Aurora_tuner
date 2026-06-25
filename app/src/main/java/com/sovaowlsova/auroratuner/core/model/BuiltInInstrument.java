package com.sovaowlsova.auroratuner.core.model;

import android.content.Context;

import java.util.List;

public class BuiltInInstrument extends Instrument {
    private final int nameResId;

    public BuiltInInstrument(String id, int nameResId) {
        super(id);
        this.nameResId = nameResId;
    }

    public BuiltInInstrument(String id, int nameResId, List<Tuning> tunings) {
        super(id, tunings);
        this.nameResId = nameResId;
    }

    @Override
    public String getName(Context context) {
        return context.getString(nameResId);
    }
}
