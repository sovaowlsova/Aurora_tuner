package com.sovaowlsova.auroratuner.core.model;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sovaowlsova.auroratuner.core.data.Note;
import com.sovaowlsova.auroratuner.core.data.TuningDirection;
import com.sovaowlsova.auroratuner.tuner.ui.TunerNoteInfo;

public abstract class InstrumentFragment extends Fragment {
    protected MutableLiveData<TuningDirection> tuningDirectionData;
    protected MutableLiveData<String> deltaTextData;

    public abstract void setTuning(Tuning tuning);
    public abstract void setNote(TunerNoteInfo info);
    protected abstract void resetUi();

    protected void updateTuningDirection(Note closestString, Note note) {
        if (tuningDirectionData == null) {
            return;
        }

        if (closestString.equals(note)) {
            tuningDirectionData.postValue(TuningDirection.HIT);
            return;
        }
        String signedDelta = closestString.getSignedDelta(note.getFrequency(), 2);
        if (signedDelta.charAt(0) == '+') {
            tuningDirectionData.postValue(TuningDirection.FLAT);
        } else {
            tuningDirectionData.postValue(TuningDirection.SHARP);
        }
    }

    protected void updateDeltaText(Note closestString, double frequency) {
        if (deltaTextData == null) {
            return;
        }

        String deltaText = closestString.getSignedDelta(frequency, 2);
        deltaTextData.postValue(deltaText);
    }

    public LiveData<TuningDirection> getTuningDirectionData() {
        if (tuningDirectionData == null) {
            tuningDirectionData = new MutableLiveData<>();
        }
        return tuningDirectionData;
    }

    public LiveData<String> getDeltaTextData() {
        if (deltaTextData == null) {
            deltaTextData = new MutableLiveData<>();
        }
        return deltaTextData;
    }
}
