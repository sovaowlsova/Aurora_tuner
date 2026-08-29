package com.sovaowlsova.auroratuner.tuner.ui.instruments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sovaowlsova.auroratuner.R;
import com.sovaowlsova.auroratuner.core.model.Instrument;
import com.sovaowlsova.auroratuner.core.model.InstrumentFragment;

public class GeneratedInstrument extends InstrumentFragment {
    private int[] stringCircleIds;
    private int[] stringTextViewIds;
    public GeneratedInstrument(Instrument instrument) {
        int countOfStrings = instrument.getStringsAmount();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_generated_instrument, container, false);
    }

    @Override
    protected int[] getStringCircleIds() {
        return stringCircleIds;
    }

    @Override
    protected int[] getStringTextViewIds() {
        return stringTextViewIds;
    }
}