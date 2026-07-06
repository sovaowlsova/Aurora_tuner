package com.sovaowlsova.auroratuner.tuner.ui;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sovaowlsova.auroratuner.R;
import com.sovaowlsova.auroratuner.core.model.InstrumentFragment;

public class GuitarFragment extends InstrumentFragment {

    public GuitarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.guitar_fragment, container, false);
    }

    @Override
    protected int[] getStringCircleIds() {
        return new int[] {
                R.id.string1,
                R.id.string2,
                R.id.string3,
                R.id.string4,
                R.id.string5,
                R.id.string6
        };
    }

    @Override
    protected int[] getStringTextViewIds() {
        return new int[] {
                R.id.note_text1,
                R.id.note_text2,
                R.id.note_text3,
                R.id.note_text4,
                R.id.note_text5,
                R.id.note_text6
        };
    }
}