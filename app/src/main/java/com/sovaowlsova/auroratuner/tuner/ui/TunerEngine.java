package com.sovaowlsova.auroratuner.tuner.ui;

import android.content.Context;

import com.sovaowlsova.auroratuner.core.data.Note;
import com.sovaowlsova.auroratuner.tuner.data.AudioCapture;
import com.sovaowlsova.auroratuner.tuner.data.SoundProcessor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TunerEngine {
    private final int sampleRate;
    private final AudioCapture audioCapture;

    public TunerEngine(AudioCapture audioCapture, int sampleRate) {
        this.audioCapture = audioCapture;
        this.sampleRate = sampleRate;
    }

    public interface Callback {
        void onNoteDetected(Note note, double frequency);
        void onAudioReadingError();
    }

    public boolean start(Context context, Callback callback) {
        boolean success = audioCapture.start(context);
        if (!success) {
            return false;
        }

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(() -> {
            short[] buffer = audioCapture.read();
            if (buffer.length == 0) {
                callback.onAudioReadingError();
                return;
            }

            double frequency = SoundProcessor.findPitch(buffer, sampleRate);
            Note note = Note.frequencyToNote(frequency);

            if (note == null) { return; }

            callback.onNoteDetected(note, frequency);
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }
}
