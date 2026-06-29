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

import com.sovaowlsova.auroratuner.R;
import com.sovaowlsova.auroratuner.core.data.BuiltInInstrumentId;
import com.sovaowlsova.auroratuner.core.data.InstrumentRegistry;
import com.sovaowlsova.auroratuner.core.di.AppContainer;
import com.sovaowlsova.auroratuner.core.model.Instrument;
import com.sovaowlsova.auroratuner.core.model.Tuning;
import com.sovaowlsova.auroratuner.tuner.presentation.TunerViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class TunerFragment extends Fragment {

    private TextView noteText;
    private TextView deltaText;
    private AutoCompleteTextView instrumentSelectionDropdown;
    private AutoCompleteTextView tuningSelectionSpinnerDropdown;
    private ConstraintSet tunerSectionConstraintSet;
    private final int INSTRUMENT_VIEW_ID = R.id.instrument_view;
    private List<Instrument> allInstruments;
    private GuitarFragment guitarFragment;
    private UkuleleFragment ukuleleFragment;
    private Fragment currentInstrumentFragment;

    private final HashMap<String, Fragment> idToFragment = new HashMap<>();
    TunerViewModel viewModel;
    TunerEngine engine;


    public TunerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            noteText.setText(R.string.note_text_placeholder);
            noteText.setTextColor(Color.WHITE);
            tunerSectionConstraintSet.setHorizontalBias(R.id.measure_line, 0.5f);
            deltaText.setText("");
            viewModel.stopTuner();
        } else {
            viewModel.startTuner(requireContext());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        guitarFragment = new GuitarFragment();
        ukuleleFragment = new UkuleleFragment();
        getChildFragmentManager()
                .beginTransaction()
                .add(INSTRUMENT_VIEW_ID, guitarFragment)
                .add(INSTRUMENT_VIEW_ID, ukuleleFragment)
                .hide(ukuleleFragment)
                .commit();
        currentInstrumentFragment = guitarFragment;

        idToFragment.put(BuiltInInstrumentId.GUITAR_6.get(), guitarFragment);
        idToFragment.put(BuiltInInstrumentId.UKULELE.get(), ukuleleFragment);

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
        setTuningSelectionSpinnerVariables(InstrumentRegistry.getInstance().getById(BuiltInInstrumentId.GUITAR_6.get()));

        viewModel.getUiState().observe(getViewLifecycleOwner(), this::setUiState);
        viewModel.getErrorState().observe(getViewLifecycleOwner(), this::displayError);
        viewModel.startTuner(requireContext());
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
        FragmentTransaction transaction = getChildFragmentManager()
                .beginTransaction()
                .hide(currentInstrumentFragment);
        transaction.setReorderingAllowed(true);
        if (instrumentId.equals(BuiltInInstrumentId.GUITAR_6.get())) {
            transaction.show(guitarFragment);
            currentInstrumentFragment = guitarFragment;
        } else if (instrumentId.equals(BuiltInInstrumentId.UKULELE.get())) {
            transaction.show(ukuleleFragment);
        }
        transaction.commit();
    }

    private void setUiState(TunerUiState state) {
        noteText.setText(state.getNoteText());
        deltaText.setText(state.getDeltaText());
        noteText.setTextColor(state.getNoteColor());
        tunerSectionConstraintSet.setHorizontalBias(R.id.measure_line, state.getLineBias());
    }

    private void displayError(String message) {
        // TODO: Display error
    }
}