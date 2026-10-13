package com.sovaowlsova.auroratuner.editor.model;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.sovaowlsova.auroratuner.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalizedNamesRVAdapter extends RecyclerView.Adapter<LocalizedNamesRVAdapter.viewholder> {
    List<LanguageItem> data = new ArrayList<>();
    private final List<String> locales;

    public LocalizedNamesRVAdapter(List<String> locales) {
        this.locales = locales;
    }

    public List<LanguageItem> getData() {
        return data;
    }

    public void addLanguage() {
        data.add(new LanguageItem());
        notifyItemInserted(data.size() - 1);
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.localized_name_item, parent, false);
        return new viewholder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                holder.itemView.getContext(),
                R.layout.dropdown_item,
                locales
        );
        adapter.setDropDownViewResource(R.layout.dropdown_item);
        holder.languagesDropdown.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder {
        AutoCompleteTextView languagesDropdown;
        TextInputEditText nameInput;

        public viewholder(@NonNull View itemView) {
            super(itemView);

            this.languagesDropdown = itemView.findViewById(R.id.editor_language_selection_dropdown);
            this.nameInput = itemView.findViewById(R.id.editor_localized_name_input);
        }
    }
}
