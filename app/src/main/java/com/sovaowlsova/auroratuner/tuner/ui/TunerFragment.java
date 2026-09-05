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
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.sovaowlsova.auroratuner.R;
import com.sovaowlsova.auroratuner.core.data.TuningDirection;
import com.sovaowlsova.auroratuner.core.data.InstrumentRegistry;
import com.sovaowlsova.auroratuner.core.model.BuiltInInstrument;
import com.sovaowlsova.auroratuner.core.model.Instrument;
import com.sovaowlsova.auroratuner.core.model.BuiltInInstrumentFragment;
import com.sovaowlsova.auroratuner.core.model.InstrumentFragment;
import com.sovaowlsova.auroratuner.core.util.FragmentFactory;
import com.sovaowlsova.auroratuner.tuner.presentation.TunerViewModel;
import com.sovaowlsova.auroratuner.tuner.ui.instruments.GeneratedInstrumentFragment;

import java.util.List;
import java.util.stream.Collectors;

public class TunerFragment extends Fragment {
    private TextView noteText;
    private TextView deltaText;
    private TextView directionSharpText;
    private TextView directionFlatText;
    private AutoCompleteTextView instrumentSelectionDropdown;
    private AutoCompleteTextView tunerSelectionDropdown;
    private ConstraintSet tunerSectionConstraintSet;
    private final int INSTRUMENT_VIEW_ID = R.id.instrument_view;
    private Instrument currentInstrument;
    private InstrumentFragment currentInstrumentFragment;
    private TunerViewModel viewModel;


    public TunerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (noteText == null) {
            return;
        }
        if (hidden) {
            // Resetting tuner to default on hide
            noteText.setText(R.string.note_text_placeholder);
            noteText.setTextColor(Color.WHITE);
            tunerSectionConstraintSet.setHorizontalBias(R.id.measure_line, 0.5f);
            deltaText.setText("");
            viewModel.stopTuner();
            directionSharpText.setText("");
            directionFlatText.setText("");
        } else {
            System.out.println("Tuner shown");
            viewModel.startTuner(requireContext());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isHidden()) {
            viewModel.startTuner(getContext());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("Creating tuner...");
        super.onCreate(savedInstanceState);
        SavedStateViewModelFactory viewModelFactory = new SavedStateViewModelFactory(
                requireActivity().getApplication(),
                this,
                getArguments());
        System.out.println("viewModel is null: " + (viewModel == null));
        viewModel = new ViewModelProvider(this, viewModelFactory).get(TunerViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tuner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setContextualVariables();

        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();
        viewModel.getUiState().observe(lifecycleOwner, this::setUiState);
        viewModel.getErrorState().observe(lifecycleOwner, this::displayError);
        viewModel.getInstrumentSpinnerState().observe(lifecycleOwner, this::setInstrumentSpinnerItems);
        viewModel.getTuningSpinnerState().observe(lifecycleOwner, this::setTuningSpinnerItems);
        viewModel.getCurrentInstrumentFragmentState().observe(lifecycleOwner, this::setInstrumentFragment);
        viewModel.getNoteState().observe(lifecycleOwner, this::setNote);
        viewModel.getCurrentTuningState().observe(lifecycleOwner, this::setTuning);

        configureInstrumentSelectionDropdown();
        configureTuningSelectionDropdown();

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
        tunerSelectionDropdown = view.findViewById(R.id.tuning_selection_dropdown);
        directionSharpText = view.findViewById(R.id.direction_sharp);
        directionFlatText = view.findViewById(R.id.direction_flat);

        ConstraintLayout tunerSectionConstraintLayout = view.findViewById(R.id.tuner_section_layout);
        tunerSectionConstraintSet = new ConstraintSet();
        tunerSectionConstraintSet.clone(tunerSectionConstraintLayout);
        tunerSectionConstraintLayout.setConstraintSet(tunerSectionConstraintSet);
    }

    @SuppressWarnings("ConstantConditions")
    private void configureInstrumentSelectionDropdown() {
        instrumentSelectionDropdown.setOnItemClickListener((parent, view, position, id) -> {
            viewModel.selectInstrument(position);
            instrumentSelectionDropdown.clearFocus();
        });
    }

    private void configureTuningSelectionDropdown() {
        tunerSelectionDropdown.setOnItemClickListener((parent, view, position, id) -> {
            // Not tracking current position so user can reset current tuning by clicking on in the dropdown
            viewModel.selectTuning(position);
            tunerSelectionDropdown.clearFocus();
            resetUiState();
        });
    }

    private void setUiState(@NonNull TunerUiState state) {
        noteText.setText(state.getNoteText());
        noteText.setTextColor(state.getNoteColor());
        tunerSectionConstraintSet.setHorizontalBias(R.id.measure_line, state.getLineBias());
    }

    private void resetUiState() {
        noteText.setText(R.string.note_text_placeholder);
        deltaText.setText("");
        noteText.setTextColor(Color.WHITE);
        tunerSectionConstraintSet.setHorizontalBias(R.id.measure_line, 0.5f);
        directionFlatText.setText("");
        directionSharpText.setText("");
    }

    private void setDeltaText(String text) {
        deltaText.setText(text);
    }

    private void setNote(@NonNull TunerNoteInfo info) {
        if (currentInstrumentFragment != null) {
            currentInstrumentFragment.setNote(info);
        }
    }

    private void setTuning(int position) {
        currentInstrumentFragment.setTuning(currentInstrument.getTunings().get(position));
        noteText.setText(R.string.note_text_placeholder);
        tunerSelectionDropdown.clearFocus();
        resetUiState();
    }

    private void displayError(@NonNull String message) {
        Context context = getContext();
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void setInstrumentSpinnerItems(@NonNull List<String> instrumentIds) {
        Context context = requireContext();
        List<String> instrumentNames = instrumentIds.stream()
                .map(id -> InstrumentRegistry.getInstance().getById(id).getName(context))
                .collect(Collectors.toList());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context,
                R.layout.dropdown_item,
                instrumentNames
        );
        adapter.setDropDownViewResource(R.layout.dropdown_item);
        instrumentSelectionDropdown.setAdapter(adapter);
    }

    private void setTuningSpinnerItems(@NonNull List<String> tuningNames) {
        int indexOfStandardTuning = tuningNames.indexOf("Standard");
        if (indexOfStandardTuning >= 0) {
            tuningNames.set(indexOfStandardTuning, requireContext().getString(R.string.standard_tuning));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.dropdown_item,
                tuningNames
        );
        adapter.setDropDownViewResource(R.layout.dropdown_item);
        tunerSelectionDropdown.setAdapter(adapter);
        tunerSelectionDropdown.setText(tuningNames.get(0), false);
    }

    private void setInstrumentFragment(@NonNull Instrument instrument) {
        System.out.println("Switch to instrument with id: " + instrument.getId());
        if (instrument.equals(currentInstrument)) {
            System.out.println("Already chose that instrument");
            return;
        }

        String instrumentTag = instrument instanceof BuiltInInstrument ? "instrument_" + instrument.getId() : "json_instrument_fragment";
        InstrumentFragment instrumentFragment = (InstrumentFragment) getChildFragmentManager().findFragmentByTag(instrumentTag);
        if (instrumentFragment == null) {
            if (instrument instanceof BuiltInInstrument builtInInstrument) {
                instrumentFragment = (BuiltInInstrumentFragment) FragmentFactory.create(builtInInstrument.getInstrumentFragmentClass());
                System.out.println("Instrument is built in");
            } else {
                instrumentFragment = new GeneratedInstrumentFragment(instrument.getTunings().get(0));
                System.out.println("Instrument is generated");
            }
        } else {
            System.out.println("Instrument fragment already exists. Connecting live data...");
        }
        instrumentFragment.getTuningDirectionData().observe(getViewLifecycleOwner(), this::setTuningDirection);
        instrumentFragment.getDeltaTextData().observe(getViewLifecycleOwner(), this::setDeltaText);

        if (!instrumentFragment.isAdded()) {
            System.out.println("Instrument is not added, adding new instrument fragment with tag: " + instrumentTag);
            getChildFragmentManager().beginTransaction()
                    .add(INSTRUMENT_VIEW_ID, instrumentFragment, instrumentTag)
                    .commit();
        }

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (currentInstrumentFragment == null) {
            System.out.println("No current instrument. New current instrument id: " + instrument.getId());
            currentInstrumentFragment = instrumentFragment;
        } else {
            System.out.println("Hiding current instrument id: " + instrument.getId());
            transaction.hide(currentInstrumentFragment);
        }
        transaction.setReorderingAllowed(true);
        transaction.show(instrumentFragment);
        transaction.commit();
        currentInstrumentFragment = instrumentFragment;
        currentInstrument = instrument;
        viewModel.selectTuning(0);
        resetUiState();
        System.out.println("End of instrument switch");
    }

    private void setTuningDirection(TuningDirection direction) {
        if (direction == TuningDirection.SHARP) {
            directionFlatText.setText("");
            directionSharpText.setText(R.string.direction_sharp_text);
        } else if (direction == TuningDirection.FLAT) {
            directionFlatText.setText(R.string.direction_flat_text);
            directionSharpText.setText("");
        } else {
            directionFlatText.setText("");
            directionSharpText.setText("");
        }
    }
}