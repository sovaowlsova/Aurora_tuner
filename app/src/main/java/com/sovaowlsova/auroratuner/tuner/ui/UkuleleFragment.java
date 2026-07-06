package com.sovaowlsova.auroratuner.tuner.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sovaowlsova.auroratuner.R;
import com.sovaowlsova.auroratuner.core.data.Note;
import com.sovaowlsova.auroratuner.core.data.TuningDirection;
import com.sovaowlsova.auroratuner.core.model.InstrumentFragment;
import com.sovaowlsova.auroratuner.core.model.Tuning;

public class UkuleleFragment extends InstrumentFragment {

    public UkuleleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.ukulele_fragment, container, false);
    }

    @Override
    protected int[] getStringCircleIds() {
        return new int[] {
                R.id.string1,
                R.id.string2,
                R.id.string3,
                R.id.string4,
        };
    }

    @Override
    protected int[] getStringTextViewIds() {
        return new int[] {
                R.id.note_text1,
                R.id.note_text2,
                R.id.note_text3,
                R.id.note_text4,
        };
    }
}