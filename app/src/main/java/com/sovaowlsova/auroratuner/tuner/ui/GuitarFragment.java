package com.sovaowlsova.auroratuner.tuner.ui;

import android.graphics.Color;
import android.os.Bundle;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sovaowlsova.auroratuner.R;
import com.sovaowlsova.auroratuner.core.data.TuningDirection;
import com.sovaowlsova.auroratuner.core.data.Note;
import com.sovaowlsova.auroratuner.core.model.InstrumentFragment;
import com.sovaowlsova.auroratuner.core.model.Tuning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class GuitarFragment extends InstrumentFragment {
    private static final String ARG_TUNING = "tuning";
    private final HashMap<Note, List<Pair<ImageView, TextView>>> noteToUi = new HashMap<>();
    private final HashSet<Note> tunedStrings = new HashSet<>();
    private final HashSet<TextView> highlightedNoteTextViews = new HashSet<>();
    private MutableLiveData<TuningDirection> tuningDirectionData;
    private Tuning tuning;
    private Tuning firstTuning;

    private final int[] stringIds = {
            R.id.string1,
            R.id.string2,
            R.id.string3,
            R.id.string4,
            R.id.string5,
            R.id.string6
    };

    private final ImageView[] stringCircles = new ImageView[stringIds.length];

    private final int[] noteTextIds = {
            R.id.note_text1,
            R.id.note_text2,
            R.id.note_text3,
            R.id.note_text4,
            R.id.note_text5,
            R.id.note_text6
    };

    private final TextView[] stringTextViews = new TextView[noteTextIds.length];
    HashSet<Pair<ImageView, TextView>> currentNoteUi = new HashSet<>();

    public GuitarFragment() {
        // Required empty public constructor
    }

    @Override
    public LiveData<TuningDirection> getTuningDirectionData() {
        if (tuningDirectionData == null) {
            tuningDirectionData = new MutableLiveData<>();
        }
        return tuningDirectionData;
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
        return inflater.inflate(R.layout.guitar_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        View currentView = requireView();
        for (int i = 0; i < stringIds.length; i++) {
            stringCircles[i] = currentView.findViewById(stringIds[i]);
            stringTextViews[i] = currentView.findViewById(noteTextIds[i]);
        }

        if (firstTuning == null) {
            return;
        }

        setTuning(firstTuning);
        mapNotesToUi(firstTuning.getNotes());
    }

    @Override
    public void setNote(Note note, boolean isTuned) {
        Note closestString = tuning.getClosestString(note.getFrequency());

        updateTuningDirection(closestString, note);

        if (tunedStrings.contains(closestString)) {
            System.out.println("String is already tuned");
            return;
        }

        List<Pair<ImageView, TextView>> affectedStrings = noteToUi.get(closestString);
        if (affectedStrings == null || affectedStrings.isEmpty()) {
            System.out.println("Couldn't find affected strings");
            return;
        }

        if (isTuned && note.equals(closestString)) {
            tunedStrings.add(closestString);
        }

        for (Pair<ImageView, TextView> uiPair : affectedStrings) {
            int newTextColor = tunedStrings.contains(closestString) ? Color.GREEN : Color.YELLOW;
            uiPair.second.setTextColor(newTextColor);
            if (tunedStrings.contains(closestString)) {
                highlightedNoteTextViews.add(uiPair.second);
            }
        }

        if (!currentNoteUi.isEmpty()) {
            for (Pair<ImageView, TextView> uiPair : currentNoteUi) {
                if (!highlightedNoteTextViews.contains(uiPair.second) && !affectedStrings.contains(uiPair)) {
                    uiPair.second.setTextColor(Color.WHITE);
                }
            }
        }

        currentNoteUi.clear();
        currentNoteUi.addAll(affectedStrings);
    }

    @Override
    public void setTuning(Tuning tuning) {
        try {
            requireView();
        } catch (IllegalStateException e) {
            // The view is not created yet. Store the tuning so onViewCreated will handle it
            firstTuning = tuning;
            return;
        }
        List<Note> notes = tuning.getNotes();
        if (notes.size() != stringIds.length) {
            System.out.println("Wrong amount of strings in a tuning: " + tuning.getName());
            Toast.makeText(requireContext(), R.string.wrong_amount_of_notes, Toast.LENGTH_LONG)
                    .show();
            return;
        }
        this.tuning = tuning;
        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            TextView noteTextView = stringTextViews[i];
            noteTextView.setText(note.getName());
        }

        resetUi();
        mapNotesToUi(notes);
    }

    private void resetUi() {
        currentNoteUi.clear();
        tunedStrings.clear();
        highlightedNoteTextViews.clear();
        for (TextView textView : stringTextViews) {
            textView.setTextColor(Color.WHITE);
        }
    }

    private void mapNotesToUi(List<Note> notes) {
        if (notes.size() != stringIds.length) {
            System.out.println("Wrong amount of strings in a tuning: " + tuning.getName());
            Toast.makeText(requireContext(), R.string.wrong_amount_of_notes, Toast.LENGTH_LONG)
                    .show();
            return;
        }
        noteToUi.clear();
        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            Pair<ImageView, TextView> uiPair = new Pair<>(
                    stringCircles[i],
                    stringTextViews[i]
            );
            noteToUi.computeIfAbsent(note, k -> new ArrayList<>())
                    .add(uiPair);
        }
    }

    private void updateTuningDirection(Note closestString, Note note) {
        if (closestString.equals(note)) {
            tuningDirectionData.postValue(TuningDirection.HIT);
            return;
        }
        String signedDelta = closestString.getSignedDelta(note.getFrequency(), 2);
        if (signedDelta.charAt(0) == '+') {
            tuningDirectionData.postValue(TuningDirection.FLAT);
        } else {
            tuningDirectionData.postValue(TuningDirection.SHARP);
        }
    }
}