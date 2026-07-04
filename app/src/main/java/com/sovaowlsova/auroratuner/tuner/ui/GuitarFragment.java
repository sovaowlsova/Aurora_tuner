package com.sovaowlsova.auroratuner.tuner.ui;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sovaowlsova.auroratuner.R;
import com.sovaowlsova.auroratuner.core.data.Note;
import com.sovaowlsova.auroratuner.core.model.InstrumentFragment;
import com.sovaowlsova.auroratuner.core.model.Tuning;

import java.util.HashMap;
import java.util.List;

public class GuitarFragment extends InstrumentFragment {
    private static final String ARG_TUNING = "tuning";
    private HashMap<Note, ImageView> noteToImageView = new HashMap<>();
    private Tuning tuning;

    private int[] stringIds = {
            R.id.string1,
            R.id.string2,
            R.id.string3,
            R.id.string4,
            R.id.string5,
            R.id.string6
    };

    public GuitarFragment() {
        // Required empty public constructor
    }

    public static GuitarFragment newInstance(Tuning tuning) {
        GuitarFragment fragment = new GuitarFragment();
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
            if (tuning == null) {
                return;
            }

            List<Note> notes = tuning.getNotes();
            if (notes.size() != stringIds.length) {
                System.out.println("Wrong amount of strings in a tuning: " + tuning.getName());
                return;
            }
            for (int i = 0; i < notes.size(); i++) {
                Note note = notes.get(i);
                noteToImageView.put(note, requireView().findViewById(stringIds[i]));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.guitar_fragment, container, false);
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