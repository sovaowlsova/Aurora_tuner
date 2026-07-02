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
    private TextView noteText;
    private TextView deltaText;
    private AutoCompleteTextView instrumentSelectionDropdown;
    private AutoCompleteTextView tuningSelectionSpinnerDropdown;
    private ConstraintSet tunerSectionConstraintSet;
    private final int INSTRUMENT_VIEW_ID = R.id.instrument_view;
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

    private void setInstrumentFragment(@NonNull Fragment fragment) {
        if (fragment == currentInstrumentFragment) {
            return;
        }
        if (!fragment.isAdded()) {
            getChildFragmentManager().beginTransaction()
                    .add(INSTRUMENT_VIEW_ID, fragment, fragment.getClass().getSimpleName())
                    .commit();
        }
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (currentInstrumentFragment == null) {
            currentInstrumentFragment = fragment;
        } else {
            transaction.hide(currentInstrumentFragment);
        }
        transaction.setReorderingAllowed(true);
        transaction.show(fragment);
        transaction.commit();
        currentInstrumentFragment = fragment;
    }
}