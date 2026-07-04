package com.sovaowlsova.auroratuner.tuner.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sovaowlsova.auroratuner.R;
import com.sovaowlsova.auroratuner.core.data.Note;
import com.sovaowlsova.auroratuner.core.model.InstrumentFragment;
import com.sovaowlsova.auroratuner.core.model.Tuning;

public class UkuleleFragment extends InstrumentFragment {
    private static final String ARG_TUNING = "tuning";

    private Tuning tuning;


    public UkuleleFragment() {
        // Required empty public constructor
    }

    public static UkuleleFragment newInstance(Tuning tuning) {
        UkuleleFragment fragment = new UkuleleFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TUNING, tuning);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tuning = (Tuning) getArguments().getSerializable(ARG_TUNING);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.ukulele_fragment, container, false);
    }

    @Override
    public void setNote(Note note, boolean isHit) {
        Note closestString = tuning.getClosestString(note.getFrequency());
    }

    @Override
    public void setTuning(Tuning tuning) {

    }

    @Override
    protected void highlightString(int index, boolean isHit) {

    }
}