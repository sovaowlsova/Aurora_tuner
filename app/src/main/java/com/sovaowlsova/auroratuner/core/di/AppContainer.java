package com.sovaowlsova.auroratuner.core.di;

import com.sovaowlsova.auroratuner.core.util.Constants;
import com.sovaowlsova.auroratuner.tuner.data.AudioCapture;
import com.sovaowlsova.auroratuner.tuner.ui.TunerEngine;

public class AppContainer {
    private static AppContainer instance;

    TunerEngine tunerEngine;

    private AppContainer() {
        AudioCapture audioCapture = new AudioCapture(Constants.SAMPLE_RATE, Constants.BUFFER_SIZE);
        tunerEngine = new TunerEngine(audioCapture, Constants.SAMPLE_RATE);
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
