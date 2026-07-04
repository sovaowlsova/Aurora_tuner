package com.sovaowlsova.auroratuner.core.model;

import android.content.Context;

import androidx.fragment.app.Fragment;

import java.util.List;

public class BuiltInInstrument extends Instrument {
    private final int nameResId;
    private final Class<? extends InstrumentFragment> instrumentFragmentClass;

    public BuiltInInstrument(String id, int nameResId, List<Tuning> tunings, Class<? extends InstrumentFragment> instrumentFragmentClass) {
        super(id, tunings);
        this.nameResId = nameResId;
        this.instrumentFragmentClass = instrumentFragmentClass;
    }

    public Class<? extends InstrumentFragment> getInstrumentFragmentClass() {
        return instrumentFragmentClass;
    }

    @Override
    public String getName(Context context) {
        return context.getString(nameResId);
    }
}
