package com.sovaowlsova.auroratuner.core.model;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sovaowlsova.auroratuner.R;
import com.sovaowlsova.auroratuner.core.data.Note;
import com.sovaowlsova.auroratuner.core.data.TuningDirection;
import com.sovaowlsova.auroratuner.tuner.ui.TunerNoteInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public abstract class InstrumentFragment extends Fragment {
    protected static final String ARG_TUNING = "tuning";
    protected final HashMap<Note, List<Pair<ImageView, TextView>>> noteToUi = new HashMap<>();
    protected final HashSet<Note> tunedStrings = new HashSet<>();
    protected final HashSet<TextView> highlightedNoteTextViews = new HashSet<>();
    protected final HashSet<Pair<ImageView, TextView>> currentNoteUi = new HashSet<>();

    protected MutableLiveData<TuningDirection> tuningDirectionData;
    protected MutableLiveData<String> deltaTextData;

    protected Tuning tuning;
    protected Tuning firstTuning;

    protected int[] stringCircleIds;
    protected int[] stringTextViewIds;
    protected ImageView[] stringCircles;
    protected TextView[] stringTextViews;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tuning = (Tuning) getArguments().getSerializable(ARG_TUNING);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View currentView = requireView();

        stringCircleIds = getStringCircleIds();
        stringTextViewIds = getStringTextViewIds();

        stringCircles = new ImageView[stringCircleIds.length];
        stringTextViews = new TextView[stringTextViewIds.length];

        for (int i = 0; i < stringCircleIds.length; i++) {
            stringCircles[i] = currentView.findViewById(stringCircleIds[i]);
            stringTextViews[i] = currentView.findViewById(stringTextViewIds[i]);
        }

        if (firstTuning == null) {
            return;
        }

        setTuning(firstTuning);
        mapNotesToUi(firstTuning.getNotes());
    }

    public void setTuning(Tuning tuning) {
        try {
            requireView();
        } catch (IllegalStateException e) {
            // The view is not created yet. Store the tuning so onViewCreated will handle it
            firstTuning = tuning;
            return;
        }
        List<Note> notes = tuning.getNotes();
        if (notes.size() != stringCircleIds.length) {
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

    public void setNote(TunerNoteInfo info) {
        Note note = info.getNote();
        Boolean isTuned = info.getInTune();

        Note closestString = tuning.getClosestString(note.getFrequency());

        updateTuningDirection(closestString, note);
        updateDeltaText(closestString, info.getFrequency());

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

    public LiveData<TuningDirection> getTuningDirectionData() {
        if (tuningDirectionData == null) {
            tuningDirectionData = new MutableLiveData<>();
        }
        return tuningDirectionData;
    }

    public LiveData<String> getDeltaTextData() {
        if (deltaTextData == null) {
            deltaTextData = new MutableLiveData<>();
        }
        return deltaTextData;
    }

    protected void mapNotesToUi(List<Note> notes) {
        if (tuning == null) {
            return;
        }
        if (notes.size() != stringCircleIds.length) {
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

    protected void resetUi() {
        currentNoteUi.clear();
        tunedStrings.clear();
        highlightedNoteTextViews.clear();
        for (TextView textView : stringTextViews) {
            textView.setTextColor(Color.WHITE);
        }
    }

    protected void updateTuningDirection(Note closestString, Note note) {
        if (tuningDirectionData == null) {
            return;
        }

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

    protected void updateDeltaText(Note closestString, double frequency) {
        if (deltaTextData == null) {
            return;
        }

        String deltaText = closestString.getSignedDelta(frequency, 2);
        deltaTextData.postValue(deltaText);
    }

    protected abstract int[] getStringCircleIds();
    protected abstract int[] getStringTextViewIds();
}
