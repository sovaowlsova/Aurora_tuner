package com.sovaowlsova.auroratuner.core.data;

import androidx.fragment.app.Fragment;

import com.sovaowlsova.auroratuner.tuner.ui.GuitarFragment;
import com.sovaowlsova.auroratuner.tuner.ui.UkuleleFragment;

public enum BuiltInInstrumentInfo {
    GUITAR_6("Guitar6", GuitarFragment.class),
    UKULELE("Ukulele", UkuleleFragment.class);

    private final String id;
    private final Class<? extends Fragment> fragmentClass;

    BuiltInInstrumentInfo(String id, Class<? extends Fragment> fragmentClass) {
        this.id = id;
        this.fragmentClass = fragmentClass;
    }

    public String getId() {
        return id;
    }

    public Class<? extends Fragment> getFragmentClass() {
        return fragmentClass;
    }
}
