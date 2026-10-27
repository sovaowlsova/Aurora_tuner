package com.sovaowlsova.auroratuner.editor.ui;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sovaowlsova.auroratuner.R;
import com.sovaowlsova.auroratuner.editor.model.ExportTuningPayload;
import com.sovaowlsova.auroratuner.editor.model.InputErrorPayload;

public class EditorViewModel extends ViewModel {
    private MutableLiveData<InputErrorPayload> inputErrorData;

    public void exportTuningToJson(ExportTuningPayload tuningPayload) {
        System.out.println("Clicked export button");
        System.out.println("instrument_id: " + tuningPayload.instrumentId());
        System.out.println("tuning_id: " + tuningPayload.tuningId());
        System.out.println("author: " + tuningPayload.authorNickname());

        if (!validateInput(tuningPayload)) return;

        System.out.println("All valid");
    }

    public LiveData<InputErrorPayload> getInputErrorData() {
        if (inputErrorData == null) {
            inputErrorData = new MutableLiveData<>();
        }
        return inputErrorData;
    }

    private boolean validateInput(ExportTuningPayload tuningPayload) {
        boolean result = true;
        int tuningIdErrorId = View.NO_ID;
        int instrumentIdErrorId = View.NO_ID;
        int localizedInstrumentNamesErrorId = tuningPayload.localizedInstrumentNames().validateData();
        int localizedTuningNamesErrorId = tuningPayload.localizedTuningNames().validateData();

        if (localizedTuningNamesErrorId != View.NO_ID || localizedInstrumentNamesErrorId != View.NO_ID) {
            result = false;
        }

        if (tuningPayload.tuningId() == null || tuningPayload.tuningId().isBlank()) {
            tuningIdErrorId = R.string.editor_empty_input_error;
            result = false;
        }

        if (tuningPayload.instrumentId() == null || tuningPayload.instrumentId().isBlank()) {
            instrumentIdErrorId = R.string.editor_empty_input_error;
            result = false;
        }

        inputErrorData.postValue(new InputErrorPayload(
                instrumentIdErrorId,
                tuningIdErrorId,
                localizedInstrumentNamesErrorId,
                localizedTuningNamesErrorId
        ));

        return result;
    }
}
