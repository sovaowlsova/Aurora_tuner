package com.sovaowlsova.auroratuner.core.model;

import android.content.Context;

import androidx.fragment.app.Fragment;

import java.util.List;

public class BuiltInInstrument extends Instrument {
    private final int nameResId;
    private final Class<? extends Fragment> instrumentFragmentClass;

    public BuiltInInstrument(String id, int nameResId, List<Tuning> tunings, Class<? extends Fragment> instrumentFragmentClass) {
        super(id, tunings);
        this.nameResId = nameResId;
        this.instrumentFragmentClass = instrumentFragmentClass;
    }

    public Class<? extends Fragment> getInstrumentFragmentClass() {
        return instrumentFragmentClass;
    }

    @Override
    public String getName(Context context) {
        return context.getString(nameResId);
    }
}
