package com.sovaowlsova.auroratuner.core.model;

import android.content.Context;

import java.util.List;

public class BuiltInInstrument extends Instrument {
    private final int nameResId;
    private final Class<? extends BuiltInInstrumentFragment> instrumentFragmentClass;

    public BuiltInInstrument(String id, int nameResId, int countOfStrings, List<Tuning> tunings, Class<? extends BuiltInInstrumentFragment> instrumentFragmentClass) {
        super(id, countOfStrings, tunings);
        this.nameResId = nameResId;
        this.instrumentFragmentClass = instrumentFragmentClass;
    }

    public Class<? extends BuiltInInstrumentFragment> getInstrumentFragmentClass() {
        return instrumentFragmentClass;
    }

    @Override
    public String getName(Context context) {
        return context.getString(nameResId);
    }
}
