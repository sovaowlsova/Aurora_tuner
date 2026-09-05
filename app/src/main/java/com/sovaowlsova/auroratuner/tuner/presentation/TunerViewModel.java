package com.sovaowlsova.auroratuner.tuner.presentation;

import android.content.Context;
import android.graphics.Color;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.sovaowlsova.auroratuner.R;
import com.sovaowlsova.auroratuner.core.data.InstrumentRegistry;
import com.sovaowlsova.auroratuner.core.data.Note;
import com.sovaowlsova.auroratuner.core.di.AppContainer;
import com.sovaowlsova.auroratuner.core.model.Instrument;
import com.sovaowlsova.auroratuner.core.model.Tuning;
import com.sovaowlsova.auroratuner.tuner.ui.TunerEngine;
import com.sovaowlsova.auroratuner.tuner.ui.TunerNoteInfo;
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
    private  MutableLiveData<TunerUiState> uiState;
    private  MutableLiveData<String> errorState;
    private MutableLiveData<List<String>> instrumentSpinnerState;
    private MutableLiveData<List<String>> tuningSpinnerState;
    private MutableLiveData<Instrument> currentInstrumentFragmentState;
    private MutableLiveData<Integer> currentTuningState;
    private MutableLiveData<TunerNoteInfo> noteState;
    private final TunerEngine engine;
    private final List<Instrument> allInstruments;
    private final SavedStateHandle savedStateHandle;
    Integer currentInstrumentPosition;
    String KEY_CURRENT_INSTRUMENT_POSITON = "current_instrument";
    String KEY_CURRENT_TUNING_POSITION = "current_tuning";

    public TunerViewModel(SavedStateHandle savedStateHandle) {
        engine = AppContainer.getInstance().getTunerEngine();
        this.savedStateHandle = savedStateHandle;
        allInstruments = InstrumentRegistry.getInstance().getAll();

        getInstrumentSpinnerState();
        setInstrumentSpinnerState();

        getTuningSpinnerState();
        getCurrentInstrumentFragmentState();
        getCurrentTuningState();

        Integer selectedInstrumentPosition = Objects.requireNonNullElse(
                savedStateHandle.get(KEY_CURRENT_INSTRUMENT_POSITON),
                0
        );
        Integer selectedTuningPosition = Objects.requireNonNullElse(
                savedStateHandle.get(KEY_CURRENT_TUNING_POSITION),
                0
        );
        System.out.println("Selected instrument position: " + selectedInstrumentPosition);
        selectInstrument(selectedInstrumentPosition);
        selectTuning(selectedTuningPosition);
    }

    public void startTuner(Context context) {
        boolean success = engine.startTuner(context, new TunerEngine.Callback() {
            @Override
            public void onNoteDetected(Note note, double frequency) {
                boolean inTune = isInTune(frequency, note);
                TunerUiState newUiState = mapToUiState(frequency, note, inTune);
                uiState.postValue(newUiState);
                TunerNoteInfo noteInfo = new TunerNoteInfo(
                        note,
                        inTune,
                        frequency
                );
                noteState.postValue(noteInfo);
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
            return;
        }
        currentInstrumentFragmentState.postValue(selectedInstrument);
        savedStateHandle.set(KEY_CURRENT_INSTRUMENT_POSITON, position);
        if (currentInstrumentPosition == null) {
            currentInstrumentPosition = position;
            setTuningSpinnerState(selectedInstrument);
        } else if (currentInstrumentPosition != position) {
            currentInstrumentPosition = position;
            setTuningSpinnerState(selectedInstrument);
        } else {
            System.out.println("No need to set new tunings since the instrument is the same");
        }
    }

    public void selectTuning(int position) {
        savedStateHandle.set(KEY_CURRENT_TUNING_POSITION, position);
        currentTuningState.postValue(position);
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

    public LiveData<TunerNoteInfo> getNoteState() {
        if (noteState == null) {
            noteState = new MutableLiveData<>();
        }
        return noteState;
    }

    public LiveData<Instrument> getCurrentInstrumentFragmentState() {
        if (currentInstrumentFragmentState == null) {
            currentInstrumentFragmentState = new MutableLiveData<>();
        }
        return currentInstrumentFragmentState;
    }

    public LiveData<Integer> getCurrentTuningState() {
        if (currentTuningState == null) {
            currentTuningState = new MutableLiveData<>();
        }
        return currentTuningState;
    }

    private TunerUiState mapToUiState(double frequency, Note note, boolean inTune) {
        int noteColor = inTune ? Color.GREEN : Color.WHITE;
        float lineBias = calculateLineBias(frequency, note);
        String noteText = note.getName();

        return new TunerUiState(noteText, noteColor, lineBias);
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

    private boolean isInTune(double frequency, Note note) {
        float cents = (float) note.getDelta(frequency);
        return Math.abs(cents) < 0.5f;
    }
}
