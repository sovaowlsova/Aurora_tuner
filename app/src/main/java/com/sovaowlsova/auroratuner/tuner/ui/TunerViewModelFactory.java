package com.sovaowlsova.auroratuner.tuner.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.lifecycle.SavedStateHandle;

import com.sovaowlsova.auroratuner.tuner.presentation.TunerViewModel;

public class TunerViewModelFactory implements ViewModelProvider.Factory {
    private final SavedStateHandle savedStateHandle;
    private final TunerEngine tunerEngine;

    public TunerViewModelFactory(SavedStateHandle savedStateHandle, TunerEngine tunerEngine) {
        this.savedStateHandle = savedStateHandle;
        this.tunerEngine = tunerEngine;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TunerViewModel.class)) {
            return (T) new TunerViewModel(savedStateHandle);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
