package com.sovaowlsova.auroratuner.tuner.ui.instruments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sovaowlsova.auroratuner.R;
import com.sovaowlsova.auroratuner.core.model.BuiltInInstrumentFragment;
import com.sovaowlsova.auroratuner.core.model.InstrumentFragment;
import com.sovaowlsova.auroratuner.core.model.Tuning;
import com.sovaowlsova.auroratuner.tuner.ui.TunerNoteInfo;

public class GeneratedInstrument extends InstrumentFragment {
    private RecyclerView recyclerView;
    private int[] stringCircleIds;
    private int[] stringTextViewIds;
    Tuning tuning;
    public GeneratedInstrument(Tuning tuning) {
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
        configureRecyclerView();
    }

    private void configureRecyclerView() {
        GeneratedNotesRVAdapter adapter = new GeneratedNotesRVAdapter(tuning);
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @SuppressWarnings("ConstantConditions")
    private void setContextualVariables() {
        recyclerView = getView().findViewById(R.id.generated_notes_recycle_view);
    }

    @Override
    public void setTuning(Tuning tuning) {

    }

    @Override
    public void setNote(TunerNoteInfo info) {

    }

    @Override
    protected void resetUi() {

    }
}