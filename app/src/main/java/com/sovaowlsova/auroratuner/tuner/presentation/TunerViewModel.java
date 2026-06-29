package com.sovaowlsova.auroratuner.tuner.presentation;

import android.content.Context;
import android.graphics.Color;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sovaowlsova.auroratuner.R;
import com.sovaowlsova.auroratuner.core.data.Note;
import com.sovaowlsova.auroratuner.tuner.ui.TunerEngine;
import com.sovaowlsova.auroratuner.tuner.ui.TunerUiState;

public class TunerViewModel extends ViewModel {
    private final float MAX_LINE_BIAS = 0.895f;
    private final float MIN_LINE_BIAS = 0.105f;
    private final float MAX_NOTE_DELTA = 0.03f;

    private final float BIAS_INTERVAL = MAX_LINE_BIAS - 0.5f;

    private  MutableLiveData<TunerUiState> uiState;
    private  MutableLiveData<String> errorState;
    private final TunerEngine engine;

    public TunerViewModel(TunerEngine engine) {
        this.engine = engine;
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
        float percentsDelta = (float) note.getPercentsDelta(frequency);
        float invertedPercentsDelta = percentsDelta * -1;
        float mappedPercentsDelta = invertedPercentsDelta / MAX_NOTE_DELTA;
        float lineBias = 0.5f + BIAS_INTERVAL * mappedPercentsDelta;
        return Math.max(MIN_LINE_BIAS, Math.min(MAX_LINE_BIAS, lineBias));
    }
}
