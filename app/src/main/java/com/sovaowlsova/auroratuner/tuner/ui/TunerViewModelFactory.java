package com.sovaowlsova.auroratuner.tuner.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.sovaowlsova.auroratuner.tuner.presentation.TunerViewModel;

public class TunerViewModelFactory implements ViewModelProvider.Factory {
    private final TunerEngine tunerEngine;

    public TunerViewModelFactory(TunerEngine tunerEngine) {
        this.tunerEngine = tunerEngine;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TunerViewModel.class)) {
            return (T) new TunerViewModel(tunerEngine);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
