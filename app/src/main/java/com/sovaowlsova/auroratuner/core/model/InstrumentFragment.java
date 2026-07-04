package com.sovaowlsova.auroratuner.core.model;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sovaowlsova.auroratuner.core.data.Note;
import com.sovaowlsova.auroratuner.core.data.TuningDirection;

public abstract class InstrumentFragment extends Fragment {
    public abstract void setNote(Note note, boolean isHit);
    public abstract void setTuning(Tuning tuning);
    public abstract LiveData<TuningDirection> getTuningDirectionData();
}
