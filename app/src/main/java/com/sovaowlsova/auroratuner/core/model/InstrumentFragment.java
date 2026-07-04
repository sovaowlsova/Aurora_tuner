package com.sovaowlsova.auroratuner.core.model;

import androidx.fragment.app.Fragment;

import com.sovaowlsova.auroratuner.core.data.Note;

public abstract class InstrumentFragment extends Fragment {
    public abstract void setNote(Note note, boolean isHit);
    public abstract void setTuning(Tuning tuning);
}
