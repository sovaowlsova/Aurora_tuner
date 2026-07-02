package com.sovaowlsova.auroratuner.tuner.presentation;

import android.content.Context;
import android.graphics.Color;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.sovaowlsova.auroratuner.R;
import com.sovaowlsova.auroratuner.core.data.BuiltInInstrumentInfo;
import com.sovaowlsova.auroratuner.core.data.InstrumentRegistry;
import com.sovaowlsova.auroratuner.core.data.Note;
import com.sovaowlsova.auroratuner.core.di.AppContainer;
import com.sovaowlsova.auroratuner.core.model.Instrument;
import com.sovaowlsova.auroratuner.core.model.Tuning;
import com.sovaowlsova.auroratuner.core.util.FragmentFactory;
import com.sovaowlsova.auroratuner.tuner.ui.TunerEngine;
import com.sovaowlsova.auroratuner.tuner.ui.TunerUiState;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TunerViewModel extends ViewModel {
     // Suppressing warnings because it's easier to change them that way
    @SuppressWarnings("FieldCanBeLocal")
    private final float MAX_LINE_BIAS = 0.895f;

    @SuppressWarnings("FieldCanBeLocal")
    private final float MIN_LINE_BIAS = 0.105f;

    @SuppressWarnings("FieldCanBeLocal")
    private final float MAX_NOTE_DELTA = 0.03f;

    @SuppressWarnings("FieldCanBeLocal")
    private final float BIAS_INTERVAL = MAX_LINE_BIAS - 0.5f;
    private final HashMap<String, Fragment> instrumentIdToFragment;
    private  MutableLiveData<TunerUiState> uiState;
    private  MutableLiveData<String> errorState;
    private MutableLiveData<List<String>> instrumentSpinnerState;
    private MutableLiveData<List<String>> tuningSpinnerState;
    private MutableLiveData<Fragment> currentInstrumentFragmentState;
    private final TunerEngine engine;
    private final List<Instrument> allInstruments;
    private final SavedStateHandle savedStateHandle;
    String KEY_CURRENT_INSTRUMENT_POSITON = "current_instrument";

    public TunerViewModel(SavedStateHandle savedStateHandle) {
        engine = AppContainer.getInstance().getTunerEngine();
        this.savedStateHandle = savedStateHandle;
        allInstruments = InstrumentRegistry.getInstance().getAll();

        instrumentIdToFragment = new HashMap<>();
        for (BuiltInInstrumentInfo info : BuiltInInstrumentInfo.values()) {
            instrumentIdToFragment.put(
                    info.getId(),
                    FragmentFactory.create(info.getFragmentClass())
            );
        }
        getInstrumentSpinnerState();
        setInstrumentSpinnerState();

        getTuningSpinnerState();
        getCurrentInstrumentFragmentState();

        Integer selectedPosition = Objects.requireNonNullElse(
                savedStateHandle.get(KEY_CURRENT_INSTRUMENT_POSITON),
                0
        );
        System.out.println("Selected instrument position: " + selectedPosition);
        selectInstrument(selectedPosition);
    }

    public void startTuner(Context context) {
        boolean success = engine.startTuner(context, new TunerEngine.Callback() {
            @Override
            public void onNoteDetected(Note note, double frequency) {
                TunerUiState newUiState = mapToUiState(frequency, note);
                uiState.postValue(newUiState);
            }

            @Override
            public void onAudioReadingError() {
                errorState.postValue(context.getString(R.string.audio_reading_error));
            }
        });

        if (!success) {
            errorState.postValue(context.getString(R.string.audio_permission_error));
        }
    }

    public void stopTuner() {
        engine.stopTuner();
    }

    public void selectInstrument(int position) {
        Instrument selectedInstrument = allInstruments.get(position);
        if (selectedInstrument == null) {
            System.out.println("Unknown instrument with position: " + position);
        }
        // Id can not be null
        @SuppressWarnings("ConstantConditions")
        String instrumentId = selectedInstrument.getId();
        Fragment newFragment = instrumentIdToFragment.get(instrumentId);
        if (newFragment == null) {
            System.out.println("instrumentId " + instrumentId + " does not correlate to any fragment");
        }
        currentInstrumentFragmentState.postValue(instrumentIdToFragment.get(instrumentId));
        savedStateHandle.set(KEY_CURRENT_INSTRUMENT_POSITON, position);
        setTuningSpinnerState(selectedInstrument);
    }

    public LiveData<TunerUiState> getUiState() {
        if (uiState == null) {
            uiState = new MutableLiveData<>();
        }
        return uiState;
    }

    public LiveData<String> getErrorState() {
        if (errorState == null) {
            errorState = new MutableLiveData<>();
        }
        return errorState;
    }

    public LiveData<List<String>> getInstrumentSpinnerState() {
        if (instrumentSpinnerState == null) {
            instrumentSpinnerState = new MutableLiveData<>();
        }
        return instrumentSpinnerState;
    }

    public LiveData<List<String>> getTuningSpinnerState() {
        if (tuningSpinnerState == null) {
            tuningSpinnerState = new MutableLiveData<>();
        }
        return tuningSpinnerState;
    }

    public LiveData<Fragment> getCurrentInstrumentFragmentState() {
        if (currentInstrumentFragmentState == null) {
            currentInstrumentFragmentState = new MutableLiveData<>();
        }
        return currentInstrumentFragmentState;
    }

    private TunerUiState mapToUiState(double frequency, Note note) {
        float cents = (float) note.getDelta(frequency);
        boolean inTune = Math.abs(cents) < 0.5f;
        int noteColor = inTune ? Color.GREEN : Color.WHITE;
        float lineBias = calculateLineBias(frequency, note);
        String noteText = note.getName();
        String deltaText = note.getSignedDelta(frequency, 2);

        return new TunerUiState(noteText, deltaText, noteColor, lineBias);
    }

    private float calculateLineBias(double frequency, Note note) {
        float percentsDelta = note.getPercentsDelta(frequency);
        float invertedPercentsDelta = percentsDelta * -1;
        float mappedPercentsDelta = invertedPercentsDelta / MAX_NOTE_DELTA;
        float lineBias = 0.5f + BIAS_INTERVAL * mappedPercentsDelta;
        return Math.max(MIN_LINE_BIAS, Math.min(MAX_LINE_BIAS, lineBias));
    }

    private void setInstrumentSpinnerState() {
        List<String> instrumentIds = allInstruments.stream()
                .map(Instrument::getId)
                .collect(Collectors.toList());
        instrumentSpinnerState.postValue(instrumentIds);
    }

    private void setTuningSpinnerState(Instrument instrument) {
        List<String> tuningNames = instrument.getTunings().stream()
                .map(Tuning::getName)
                .collect(Collectors.toList());
        tuningSpinnerState.postValue(tuningNames);
    }
}
