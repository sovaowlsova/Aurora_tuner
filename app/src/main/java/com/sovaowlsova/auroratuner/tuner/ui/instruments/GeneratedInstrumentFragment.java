package com.sovaowlsova.auroratuner.tuner.ui.instruments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sovaowlsova.auroratuner.R;
import com.sovaowlsova.auroratuner.core.data.Note;
import com.sovaowlsova.auroratuner.core.model.InstrumentFragment;
import com.sovaowlsova.auroratuner.core.model.Tuning;
import com.sovaowlsova.auroratuner.tuner.ui.TunerNoteInfo;

import java.util.HashSet;

public class GeneratedInstrumentFragment extends InstrumentFragment {
    private RecyclerView recyclerView;
    private TextView noTuningsErrorView;
    GeneratedNotesRVAdapter rvAdapter;
    Tuning tuning;
    public GeneratedInstrumentFragment(Tuning tuning) {
        this.tuning = tuning;
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setContextualVariables();
        if (tuning == null) {
            recyclerView.setVisibility(View.GONE);
            noTuningsErrorView.setVisibility(View.VISIBLE);
            return;
        }
        configureRecyclerView();
    }

    private void configureRecyclerView() {
        rvAdapter = new GeneratedNotesRVAdapter(tuning);
        LinearLayoutManager layoutManager = new CenterLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(rvAdapter);
        recyclerView.setItemAnimator(null);
        rvAdapter.notifyDataSetChanged();
    }

    @SuppressWarnings("ConstantConditions")
    private void setContextualVariables() {
        View view = getView();
        recyclerView = view.findViewById(R.id.generated_notes_recycle_view);
        noTuningsErrorView = view.findViewById(R.id.no_tunings_text);
    }

    @Override
    public void setTuning(Tuning tuning) {
        if (rvAdapter != null) {
            rvAdapter.setTuning(tuning);
            resetUi();
        } else {
            System.out.println("Trying to set tuning to null rvAdapter");
        }
    }

    @Override
    public void setNote(TunerNoteInfo info) {
        if (rvAdapter == null) {
            System.out.println("Trying to set note with null rvAdapter");
            return;
        }

        Note note = info.note();
        Note closestString = tuning.getClosestString(note.getFrequency());

        updateTuningDirection(closestString, note);
        updateDeltaText(closestString, info.frequency());

        int firstIndexOfNote = tuning.getNotes().indexOf(closestString);
        rvAdapter.setNote(info);
        recyclerView.smoothScrollToPosition(firstIndexOfNote);
    }

    @Override
    protected void resetUi() {
        if (rvAdapter != null) {
            rvAdapter.resetUI();
        }
    }
}