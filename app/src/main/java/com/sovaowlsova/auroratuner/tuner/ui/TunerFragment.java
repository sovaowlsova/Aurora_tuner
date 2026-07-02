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
import com.sovaowlsova.auroratuner.core.data.InstrumentRegistry;
import com.sovaowlsova.auroratuner.core.di.AppContainer;
import com.sovaowlsova.auroratuner.core.model.BuiltInInstrument;
import com.sovaowlsova.auroratuner.core.model.Instrument;
import com.sovaowlsova.auroratuner.core.util.FragmentFactory;
import com.sovaowlsova.auroratuner.tuner.presentation.TunerViewModel;

import java.util.List;
import java.util.stream.Collectors;

public class TunerFragment extends Fragment {
    private TextView noteText;
    private TextView deltaText;
    private AutoCompleteTextView instrumentSelectionDropdown;
    private AutoCompleteTextView tuningSelectionSpinnerDropdown;
    private ConstraintSet tunerSectionConstraintSet;
    private final int INSTRUMENT_VIEW_ID = R.id.instrument_view;
    private Instrument currentInstrument;
    private Fragment currentInstrumentFragment;
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
        } else {
            System.out.println("Tuner shown");
            viewModel.startTuner(requireContext());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("Creating tuner...");
        super.onCreate(savedInstanceState);
        TunerEngine engine = AppContainer.getInstance().getTunerEngine();
        SavedStateViewModelFactory viewModelFactory = new SavedStateViewModelFactory(requireActivity().getApplication(),
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

        viewModel.getUiState().observe(getViewLifecycleOwner(), this::setUiState);
        viewModel.getErrorState().observe(getViewLifecycleOwner(), this::displayError);
        viewModel.getInstrumentSpinnerState().observe(getViewLifecycleOwner(), this::setInstrumentSpinnerItems);
        viewModel.getTuningSpinnerState().observe(getViewLifecycleOwner(), this::setTuningSpinnerItems);
        viewModel.getCurrentInstrumentFragmentState().observe(getViewLifecycleOwner(), this::setInstrumentFragment);

        configureInstrumentSelectionDropdown();

        System.out.println("Tuner view is created");
        if (isVisible() || !isHidden()) {
            viewModel.startTuner(requireContext());
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
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
    private void configureInstrumentSelectionDropdown() {
        instrumentSelectionDropdown.setOnItemClickListener((parent, view, position, id) -> {
            viewModel.selectInstrument(position);
        });
    }

    private void setUiState(@NonNull TunerUiState state) {
        noteText.setText(state.getNoteText());
        deltaText.setText(state.getDeltaText());
        noteText.setTextColor(state.getNoteColor());
        tunerSectionConstraintSet.setHorizontalBias(R.id.measure_line, state.getLineBias());
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.dropdown_item,
                tuningNames
        );
        adapter.setDropDownViewResource(R.layout.dropdown_item);
        tuningSelectionSpinnerDropdown.setAdapter(adapter);
    }

    private void setInstrumentFragment(@NonNull Instrument instrument) {
        System.out.println("Switch to instrument with id: " + instrument.getId());
        if (instrument.equals(currentInstrument)) {
            System.out.println("Already chose that instrument");
            return;
        }
        String instrumentTag = "instrument_" + instrument.getId();
        Fragment instrumentFragment = getChildFragmentManager().findFragmentByTag(instrumentTag);
        if (instrumentFragment == null) {
            if (instrument instanceof BuiltInInstrument builtInInstrument) {
                instrumentFragment = FragmentFactory.create(builtInInstrument.getInstrumentFragmentClass());
                System.out.println("Instrument is built in");
            } else {
                // TODO: implement JSON instruments fragments
            }
        }
        if (instrumentFragment == null) {
            System.out.println("Could not find or create fragment for instrument id: " + instrument.getId());
            return;
        }
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
        System.out.println("End of instrument switch");
    }
}