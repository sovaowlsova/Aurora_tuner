package com.sovaowlsova.auroratuner.tuner.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.sovaowlsova.auroratuner.R;
import com.sovaowlsova.auroratuner.core.data.BuiltInInstrumentInfo;
import com.sovaowlsova.auroratuner.core.data.InstrumentRegistry;
import com.sovaowlsova.auroratuner.core.di.AppContainer;
import com.sovaowlsova.auroratuner.core.model.Instrument;
import com.sovaowlsova.auroratuner.core.model.Tuning;
import com.sovaowlsova.auroratuner.core.util.FragmentFactory;
import com.sovaowlsova.auroratuner.tuner.presentation.TunerViewModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class TunerFragment extends Fragment {

    private HashMap<String, Fragment> instrumentIdToFragment;
    private TextView noteText;
    private TextView deltaText;
    private AutoCompleteTextView instrumentSelectionDropdown;
    private AutoCompleteTextView tuningSelectionSpinnerDropdown;
    private ConstraintSet tunerSectionConstraintSet;
    private final int INSTRUMENT_VIEW_ID = R.id.instrument_view;
    private List<Instrument> allInstruments;
    private Fragment currentInstrumentFragment;
    TunerViewModel viewModel;
    TunerEngine engine;


    public TunerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (noteText == null) {
            return;
        }
        if (hidden) {
            noteText.setText(R.string.note_text_placeholder);
            noteText.setTextColor(Color.WHITE);
            tunerSectionConstraintSet.setHorizontalBias(R.id.measure_line, 0.5f);
            deltaText.setText("");
            viewModel.stopTuner();
        } else {
            System.out.println("Tuner shown");
            viewModel.startTuner(requireContext());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instrumentIdToFragment = new HashMap<>();
        for (BuiltInInstrumentInfo info : BuiltInInstrumentInfo.values()) {
            instrumentIdToFragment.put(
                    info.getId(),
                    FragmentFactory.create(info.getFragmentClass())
            );
        }

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        instrumentIdToFragment.forEach((key, value) -> {
            transaction.add(INSTRUMENT_VIEW_ID, value);
            transaction.hide(value);
        });
        transaction.commit();
        switchInstrumentView(BuiltInInstrumentInfo.GUITAR_6.getId());

        engine = AppContainer.getInstance().getTunerEngine();
        TunerViewModelFactory viewModelFactory = new TunerViewModelFactory(engine);
        viewModel = new ViewModelProvider(this, viewModelFactory).get(TunerViewModel.class);
        allInstruments = InstrumentRegistry.getInstance().getAll();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tuner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setContextualVariables();
        setInstrumentSelectionSpinnerVariables();
        setTuningSelectionSpinnerVariables(InstrumentRegistry.getInstance().getById(BuiltInInstrumentInfo.GUITAR_6.getId()));

        viewModel.getUiState().observe(getViewLifecycleOwner(), this::setUiState);
        viewModel.getErrorState().observe(getViewLifecycleOwner(), this::displayError);
        System.out.println("Tuner view is created");
        if (isVisible() || !isHidden()) {
            viewModel.startTuner(requireContext());
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void setContextualVariables() {
        View view = getView();
        noteText = view.findViewById(R.id.note_text);
        deltaText = view.findViewById(R.id.delta_text);
        instrumentSelectionDropdown = view.findViewById(R.id.instrument_selection_dropdown);
        tuningSelectionSpinnerDropdown = view.findViewById(R.id.tuning_selection_dropdown);

        ConstraintLayout tunerSectionConstraintLayout = view.findViewById(R.id.tuner_section_layout);
        tunerSectionConstraintSet = new ConstraintSet();
        tunerSectionConstraintSet.clone(tunerSectionConstraintLayout);
        tunerSectionConstraintLayout.setConstraintSet(tunerSectionConstraintSet);
    }

    @SuppressWarnings("ConstantConditions")
    private void setInstrumentSelectionSpinnerVariables() {
        Context context = getContext();

        List<String> instrumentNames = InstrumentRegistry.getInstance().getAll()
                .stream()
                .map(inst -> inst.getName(context))
                .collect(Collectors.toList());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context,
                R.layout.dropdown_item,
                instrumentNames
        );

        adapter.setDropDownViewResource(R.layout.dropdown_item);
        instrumentSelectionDropdown.setAdapter(adapter);

        instrumentSelectionDropdown.setOnItemClickListener((parent, view, position, id) -> {
            Instrument selectedInstrument = allInstruments.get(position);
            String instrumentId = selectedInstrument.getId();
            switchInstrumentView(instrumentId);
            setTuningSelectionSpinnerVariables(selectedInstrument);
        });
    }

    @SuppressWarnings("ConstantConditions")
    private void setTuningSelectionSpinnerVariables(Instrument instrument) {
        Context context = getContext();
        List<Tuning> instrumentTunings = instrument.getTunings();
        // toList() required min API level 34
        List<String> tuningNames = instrumentTunings
                .stream()
                .map(Tuning::getName)
                .collect(Collectors.toList());


        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context,
                R.layout.dropdown_item,
                tuningNames
        );

        adapter.setDropDownViewResource(R.layout.dropdown_item);
        tuningSelectionSpinnerDropdown.setAdapter(adapter);
    }

    private void switchInstrumentView(String instrumentId) {
        System.out.println("Switching to instrument with id " + instrumentId);
        Fragment newFragment = instrumentIdToFragment.get(instrumentId);
        if (newFragment == null) {
            System.out.println("Unknown instrument with ID " + instrumentId);
            return;
        }
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (currentInstrumentFragment == null) {
            currentInstrumentFragment = newFragment;
        } else {
            transaction.hide(currentInstrumentFragment);
        }
        transaction.setReorderingAllowed(true);
        transaction.show(newFragment);
        transaction.commit();
        currentInstrumentFragment = newFragment;
    }

    private void setUiState(TunerUiState state) {
        noteText.setText(state.getNoteText());
        deltaText.setText(state.getDeltaText());
        noteText.setTextColor(state.getNoteColor());
        tunerSectionConstraintSet.setHorizontalBias(R.id.measure_line, state.getLineBias());
    }

    private void displayError(String message) {
        Context context = getContext();
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}