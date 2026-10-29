package com.sovaowlsova.auroratuner.editor.ui;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sovaowlsova.auroratuner.R;
import com.sovaowlsova.auroratuner.core.model.JsonTuning;
import com.sovaowlsova.auroratuner.editor.model.ExportTuningPayload;
import com.sovaowlsova.auroratuner.editor.model.InputErrorPayload;

public class EditorViewModel extends ViewModel {
    private MutableLiveData<InputErrorPayload> inputErrorData;

    public boolean exportTuningToJson(ExportTuningPayload tuningPayload) {
        System.out.println("Clicked export button");
        System.out.println("instrument_id: " + tuningPayload.instrumentId());
        System.out.println("tuning_id: " + tuningPayload.tuningId());
        System.out.println("author: " + tuningPayload.authorNickname());
        System.out.println("Notes: " + tuningPayload.notes().getData().toString());
        System.out.println("Localized instrument names:");
        tuningPayload.localizedInstrumentNames().getData().forEach((key, value) -> System.out.println(key + ": " + value));
        System.out.println("Localized tuning names:");
        tuningPayload.localizedTuningNames().getData().forEach((key, value) -> System.out.println(key + ": " + value));

        if (!validateInput(tuningPayload)) return true;
        System.out.println("All valid");

        JsonTuning jsonTuning = new JsonTuning(
                tuningPayload.instrumentId(),
                tuningPayload.tuningId(),
                tuningPayload.authorNickname(),
                tuningPayload.localizedInstrumentNames().getData(),
                tuningPayload.localizedTuningNames().getData(),
                tuningPayload.notes().getData()
        );

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String jsonResult = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonTuning);
            System.out.println(jsonResult);
        } catch (JsonProcessingException e) {
            System.out.println("Error while processing tuning to json");
            System.out.println(e.getMessage());
            return false;
        }

        return true;
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
