package com.sovaowlsova.auroratuner.core.model;

import android.content.Context;

import com.sovaowlsova.auroratuner.R;

public enum BuiltInInstruments {
    Guitar6(R.string.instrument_guitar, "Guitar6",
            new Tuning[] {BuiltInTunings.Guitar6Standard.get(), BuiltInTunings.Guitar6DropD.get()}),
    Ukulele(R.string.instrument_ukulele, "Ukulele",
            new Tuning[] {BuiltInTunings.UkuleleStandard.get()});
    private final int displayNameResId;
    private final String idName;
    private final Tuning[] builtInTunings;

    BuiltInInstruments(int displayNameResId, String idName, Tuning[] builtInTunings) {
        this.displayNameResId = displayNameResId;
        this.idName = idName;
        this.builtInTunings = builtInTunings;
    }

    public String getDisplayName(Context context) {
        return context.getResources().getString(displayNameResId);
    }

    public String getIdName() {
        return idName;
    }

    public Tuning[] getBuiltInTunings() {
        return builtInTunings.clone();
    }
}
