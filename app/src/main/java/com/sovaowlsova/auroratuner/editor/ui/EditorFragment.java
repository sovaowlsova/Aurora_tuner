package com.sovaowlsova.auroratuner.editor.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.sovaowlsova.auroratuner.R;
import com.sovaowlsova.auroratuner.editor.model.LanguageItem;
import com.sovaowlsova.auroratuner.editor.model.LocalizedNamesRVAdapter;

import java.util.Collections;
import java.util.List;

public class EditorFragment extends Fragment {
    private TextInputEditText instrumentIdInput;
    private TextInputEditText tuningIdInput;
    private TextInputEditText authorInput;
    private RecyclerView localizedInstrumentNamesRecyclerView;
    private RecyclerView localizedTuningNamesRecyclerView;
    private RecyclerView notesRecyclerView;
    private MaterialButton addInstrumentNameButton;
    private Button exportTuningButton;

    private LocalizedNamesRVAdapter instrumentNamesAdapter;


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
        setContextualVariables();
        exportTuningButton.setOnClickListener(this::exportTuningToJson);

        instrumentNamesAdapter = new LocalizedNamesRVAdapter(List.of("en", "ru", "br"));
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
    }

    private void exportTuningToJson(View view) {
        System.out.println("Clicked export button");
        System.out.println("instrument_id: " + instrumentIdInput.getText());
        System.out.println("tuning_id: " + tuningIdInput.getText());
        System.out.println("author: " + authorInput.getText());

        if (!validateInput()) {
            Toast.makeText(requireContext(), R.string.editor_invalid_input, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput() {
        boolean valid = true;
        Context context = requireContext();

        if (isInputEmpty(instrumentIdInput)) {
            valid = false;
            instrumentIdInput.setError(context.getString(R.string.editor_empty_input_error));
        }

        if (isInputEmpty(tuningIdInput)) {
            valid = false;
            tuningIdInput.setError(context.getString(R.string.editor_empty_input_error));
        }

        List<LanguageItem> instrumentNames = instrumentNamesAdapter.getData();
        System.out.println("Instrument names:");
        instrumentNames.forEach(i -> System.out.println(i.getLocale() + ": " + i.getName()));

        return valid;
    }

    private boolean isInputEmpty(TextInputEditText input) {
        return input.getText().toString().trim().isEmpty();
    }
}