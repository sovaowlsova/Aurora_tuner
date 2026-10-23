package com.sovaowlsova.auroratuner.editor.model;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.sovaowlsova.auroratuner.R;

import java.util.ArrayList;
import java.util.List;

public class LocalizedNamesRVAdapter extends RecyclerView.Adapter<LocalizedNamesRVAdapter.viewholder> {
    List<LanguageItem> data = new ArrayList<>();
    private final List<String> locales;
    private Context context;

    public LocalizedNamesRVAdapter(List<String> locales) {
        this.locales = locales;
        addLanguage();
        data.get(0).setLocale("en");
    }

    public List<LanguageItem> getData() {
        return data;
    }

    public void addLanguage() {
        // Don't make more items if all the possible locales already have a place
        if (data.size() == locales.size()) {
            Toast.makeText(context, R.string.editor_max_capacity_error, Toast.LENGTH_SHORT).show();
            return;
        }

        data.add(new LanguageItem());
        notifyItemInserted(data.size() - 1);
    }

    protected void removeLanguage(int position) {
        // Don't remove the default English language
        if (position == 0) {
            Toast.makeText(context, R.string.editor_cant_delete_default_language_error, Toast.LENGTH_SHORT).show();
            return;
        }

        data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, data.size() - position);
    }

    protected void setLocaleToLangItem(int langItemPosition, int localePosition) {
        data.get(langItemPosition).setLocale(locales.get(localePosition));
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.localized_name_item, parent, false);
        context = parent.getContext();
        return new viewholder(item, this::removeLanguage, this::setLocaleToLangItem);
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
        holder.bindToLanguageItem(data.get(position));

        if (position == 0) {
            holder.removeButton.setVisibility(View.GONE);
            holder.languagesDropdown.setText("en");
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder {
        private final AutoCompleteTextView languagesDropdown;
        private final TextInputEditText nameInput;
        private final MaterialButton removeButton;
        private TextWatcher textWatcher;
        private final int NO_ITEM_SELECTED = -1;

        public interface onRemoveClickListener {
            void onRemove(int position);
        }

        public interface onLocaleSelectedCallback {
            void onSelect(int langItemPosition, int localePosition);
        }

        public void bindToLanguageItem(LanguageItem item) {
            if (textWatcher != null) {
                nameInput.removeTextChangedListener(textWatcher);
            }

            textWatcher = new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    item.setName(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            };

            nameInput.addTextChangedListener(textWatcher);
        }

        public viewholder(@NonNull View itemView, onRemoveClickListener removeClickListener, onLocaleSelectedCallback localeSelectedCallback) {
            super(itemView);

            this.languagesDropdown = itemView.findViewById(R.id.editor_language_selection_dropdown);
            this.nameInput = itemView.findViewById(R.id.editor_localized_name_input);
            this.removeButton = itemView.findViewById(R.id.editor_remove_name_button);

            removeButton.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    removeClickListener.onRemove(position);
                }
            });

            languagesDropdown.setOnItemClickListener((adapterView, view, i, l) -> {
                localeSelectedCallback.onSelect(getBindingAdapterPosition(), i);
            });
        }
    }
}
