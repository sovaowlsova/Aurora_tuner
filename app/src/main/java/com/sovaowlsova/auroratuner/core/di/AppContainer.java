package com.sovaowlsova.auroratuner.core.di;

import com.sovaowlsova.auroratuner.tuner.data.AudioCapture;
import com.sovaowlsova.auroratuner.tuner.ui.TunerEngine;

public class AppContainer {
    private final int SAMPLE_RATE = 44100;
    private final int BUFFER_SIZE = 8192;
    private static AppContainer instance;

    TunerEngine tunerEngine;

    private AppContainer() {
        AudioCapture audioCapture = new AudioCapture(SAMPLE_RATE, BUFFER_SIZE);
        tunerEngine = new TunerEngine(audioCapture, SAMPLE_RATE);
    }

    public static AppContainer getInstance() {
        if (instance == null) {
            instance = new AppContainer();
        }
        return instance;
    }

    public TunerEngine getTunerEngine() {
        return tunerEngine;
    }
}
