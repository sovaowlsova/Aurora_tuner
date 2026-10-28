package com.sovaowlsova.auroratuner.editor.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.sovaowlsova.auroratuner.R;
import com.sovaowlsova.auroratuner.core.util.Constants;
import com.sovaowlsova.auroratuner.editor.model.ExportTuningPayload;
import com.sovaowlsova.auroratuner.editor.model.InputErrorPayload;
import com.sovaowlsova.auroratuner.editor.model.LocalizedNamesRVAdapter;
import com.sovaowlsova.auroratuner.editor.model.NotesRVAdapter;

public class EditorFragment extends Fragment {
    private TextInputEditText instrumentIdInput;
    private TextInputEditText tuningIdInput;
    private TextInputEditText authorInput;
    private TextView notesCountText;
    private TextView localizedInstrumentNamesErrorMessage;
    private TextView localizedTuningNamesErrorMessage;
    private RecyclerView localizedInstrumentNamesRecyclerView;
    private RecyclerView localizedTuningNamesRecyclerView;
    private RecyclerView notesRecyclerView;
    private MaterialButton addInstrumentNameButton;
    private MaterialButton addTuningNameButton;
    private MaterialButton addNoteButton;
    private Button exportTuningButton;
    private EditorViewModel viewModel;

    private LocalizedNamesRVAdapter instrumentNamesAdapter;
    private LocalizedNamesRVAdapter tuningNamesAdapter;
    private NotesRVAdapter notesAdapter;


    public EditorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_editor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configureViewModel();
        setContextualVariables();

        configureLocalizedInstrumentNamesAdapter();
        configureLocalizedTuningNamesAdapter();
        configureNotesAdapter();
        configureEditFields();

        subscribeToLiveData();

        exportTuningButton.setOnClickListener(this::exportTuningToJson);
    }

    private void configureViewModel() {
        SavedStateViewModelFactory viewModelFactory = new SavedStateViewModelFactory(
                requireActivity().getApplication(),
                this,
                getArguments());
        System.out.println("viewModel is null: " + (viewModel == null));
        viewModel = new ViewModelProvider(this, viewModelFactory).get(EditorViewModel.class);
    }

    private void configureLocalizedInstrumentNamesAdapter() {
        instrumentNamesAdapter = new LocalizedNamesRVAdapter(Constants.supportedLocales, requireContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        localizedInstrumentNamesRecyclerView.setLayoutManager(layoutManager);
        localizedInstrumentNamesRecyclerView.setAdapter(instrumentNamesAdapter);

        addInstrumentNameButton.setOnClickListener(view1 -> {
            instrumentNamesAdapter.addLanguage();
        });
    }

    private void configureLocalizedTuningNamesAdapter() {
        tuningNamesAdapter = new LocalizedNamesRVAdapter(Constants.supportedLocales, requireContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        localizedTuningNamesRecyclerView.setLayoutManager(layoutManager);
        localizedTuningNamesRecyclerView.setAdapter(tuningNamesAdapter);

        addTuningNameButton.setOnClickListener(view1 -> {
            tuningNamesAdapter.addLanguage();
        });
    }

    private void configureNotesAdapter() {
        notesAdapter = new NotesRVAdapter(requireContext());
        Context context = requireContext();
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
        );
        notesRecyclerView.setLayoutManager(layoutManager);
        notesRecyclerView.setAdapter(notesAdapter);

        addNoteButton.setOnClickListener(view -> {
            notesAdapter.addNote();
        });

        setTotalNotesText(notesAdapter.getItemCount());
    }

    private void setTotalNotesText(int count) {
        notesCountText.setText(requireContext().getString(R.string.editor_notes_total, count));
    }

    private void configureEditFields() {
        configureEditField(tuningIdInput);
        configureEditField(instrumentIdInput);
        configureEditField(authorInput);
    }

    private void configureEditField(TextInputEditText field) {
        field.setOnEditorActionListener(((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                clearFocusAndHideKeyboard(field);
                return true;
            }
            return false;
        }));
    }

    private void exportTuningToJson(View view) {
        Editable tuningId = tuningIdInput.getText();
        Editable instrumentId = instrumentIdInput.getText();
        Editable authorNickname = authorInput.getText();

        ExportTuningPayload payload = new ExportTuningPayload(
                stringOrNull(instrumentId),
                stringOrNull(tuningId),
                stringOrNull(authorNickname),
                instrumentNamesAdapter,
                tuningNamesAdapter,
                notesAdapter
        );

        viewModel.exportTuningToJson(payload);
    }

    private String stringOrNull(Editable editable) {
        return editable == null ? null : editable.toString();
    }
    private void setErrors(InputErrorPayload payload) {
        Context context = requireContext();
        if (payload.tuningIdErrorId() != View.NO_ID) tuningIdInput.setError(context.getString(payload.tuningIdErrorId()));
        if (payload.instrumentIdErrorId() != View.NO_ID) instrumentIdInput.setError(context.getString(payload.instrumentIdErrorId()));
        setErrorIfNotNull(payload.localizedInstrumentNamesErrorId(), localizedInstrumentNamesErrorMessage);
        setErrorIfNotNull(payload.localizedTuningNamesErrorId(), localizedTuningNamesErrorMessage);
    }

    private void setErrorIfNotNull(int errorMessageId, TextView textView) {
        if (errorMessageId == View.NO_ID) {
            textView.setVisibility(View.GONE);
            return;
        }

        textView.setText(requireContext().getString(errorMessageId));
        textView.setVisibility(View.VISIBLE);
    }

    private void setContextualVariables() {
        View view = getView();

        if (view == null) return;

        instrumentIdInput = view.findViewById(R.id.instrument_id_input);
        tuningIdInput = view.findViewById(R.id.tuning_id_input);
        authorInput = view.findViewById(R.id.author_input);
        localizedInstrumentNamesRecyclerView = view.findViewById(R.id.editor_instrument_names);
        localizedTuningNamesRecyclerView = view.findViewById(R.id.editor_tuning_names);
        notesRecyclerView = view.findViewById(R.id.editor_notes);
        addInstrumentNameButton = view.findViewById(R.id.editor_add_instrument_name_button);
        exportTuningButton = view.findViewById(R.id.editor_export_tuning_button);
        localizedInstrumentNamesErrorMessage = view.findViewById(R.id.editor_localized_names_error);
        localizedTuningNamesErrorMessage = view.findViewById(R.id.editor_localized_tuning_names_error);
        addTuningNameButton = view.findViewById(R.id.editor_add_tuning_name_button);
        addNoteButton = view.findViewById(R.id.editor_add_note_button);
        notesCountText = view.findViewById(R.id.editor_notes_count);
    }

    private void subscribeToLiveData() {
        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();
        viewModel.getInputErrorData().observe(lifecycleOwner, this::setErrors);
        notesAdapter.getDataSizeData().observe(lifecycleOwner, this::setTotalNotesText);
    }

    private void clearFocusAndHideKeyboard(TextInputEditText input) {
        input.clearFocus();

        InputMethodManager inputMethodManager = (InputMethodManager)
                input.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
        }
    }
}