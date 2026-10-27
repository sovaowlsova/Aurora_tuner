package com.sovaowlsova.auroratuner.editor.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.sovaowlsova.auroratuner.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LocalizedNamesRVAdapter extends RecyclerView.Adapter<LocalizedNamesRVAdapter.viewholder> {
    List<LanguageItem> data = new ArrayList<>();
    private final List<String> locales;
    private final Context context;
    private final Map<String, SpannableString> hashedCustomFlags = new HashMap<>();

    public LocalizedNamesRVAdapter(List<String> locales, Context context) {
        this.locales = locales;
        this.context = context;
        addLanguage();
        data.get(0).setLocale(locales.get(0));
    }

    public HashMap<String, String> getData() {
        HashMap<String, String> result = new HashMap<>();
        for (LanguageItem languageItem : data) {
            result.put(languageItem.getLocale(), languageItem.getName());
        }
        return result;
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

    public int validateData() {
        for (LanguageItem languageItem : data) {
            String locale = languageItem.getLocale();
            String name = languageItem.getName();

            if (name == null || name.isBlank()) return R.string.editor_empty_name_fields_error;
            if (locale == null || locale.isBlank()) return R.string.editor_empty_locales_error;
        }
        return View.NO_ID;
    }

    @SuppressLint("NotifyDataSetChanged")
    protected void removeLanguage(int position) {
        // Don't remove the default language
        if (position == 0) {
            Toast.makeText(context, R.string.editor_cant_delete_default_language_error, Toast.LENGTH_SHORT).show();
            return;
        }

        if (data.get(position).getLocale() != null) {
            System.out.println("Invalidating locales data");
            data.remove(position);
            notifyDataSetChanged();
        } else {
            data.remove(position);
            notifyItemRemoved(position);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    protected void chooseLangItemLocale(int langItemPosition, String selectedLocale) {
        data.get(langItemPosition).setLocale(selectedLocale);
        for (int i = 0; i < data.size(); i++) {
            notifyItemChanged(i, new LocaleRefreshPayload(getAvailableLocalesForPosition(i)));
        }
    }

    private List<SpannableString> getAvailableLocalesForPosition(int position) {
        List<String> result = new ArrayList<>();
        LanguageItem languageItem = data.get(position);

        // We are not using .addFirst() to support older APIs
        if (languageItem.getLocale() != null) {
            result.add(languageItem.getLocale());

            int currentLocaleIndex = locales.indexOf(languageItem.getLocale());
            for (int i = 0; i < locales.size(); i++) {
                if (i == currentLocaleIndex) continue;
                result.add(locales.get(i));
            }
        } else {
            result.addAll(locales);
        }

        for (int i = 0; i < data.size(); i++) {
            if (i == position) continue;
            result.remove(data.get(i).getLocale());
        }

        return result.stream().map(
                i -> i.contains("[") ? getCustomFlag(i) : new SpannableString(i))
                .collect(Collectors.toList());
    }

    private SpannableString getCustomFlag(String text) {
        SpannableString hashed = hashedCustomFlags.get(text);
        if (hashed != null) {
            System.out.println("Hashed custom flag");
            return hashed;
        }

        System.out.println("Getting custom flag for " + text);
        SpannableString spannableString = new SpannableString(text);
        String[] split = text.split(" ");
        if (split.length < 2) return spannableString;
        String code = split[1];

        int flagId = switch (code) {
            case "[TT]" -> R.drawable.tatar;
            default -> -1;
        };

        if (flagId == -1) return spannableString;

        Drawable flag = ContextCompat.getDrawable(context, flagId);
        if (flag == null) return spannableString;

        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        int flagHeightPx = (int) (16 * fontScale);
        int flagWidthPx = (int) (int) (flag.getIntrinsicWidth()
                * ((float) flagHeightPx / flag.getIntrinsicHeight()));
        flag.setBounds(0, 0, flagWidthPx, flagHeightPx);

        ImageSpan imageSpan = new ImageSpan(flag, ImageSpan.ALIGN_BASELINE);
        int start = text.indexOf("[");
        int end = text.length();

        spannableString.setSpan(imageSpan, start, end, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);

        System.out.println("Got custom flag: " + spannableString);
        hashedCustomFlags.putIfAbsent(text, spannableString);
        return spannableString;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.localized_name_item, parent, false);
        return new viewholder(item, this::removeLanguage, this::chooseLangItemLocale, this::getCustomFlag);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        List<SpannableString> availableLocales = getAvailableLocalesForPosition(position);
        ArrayAdapter<SpannableString> adapter = new ArrayAdapter<>(
                holder.itemView.getContext(),
                R.layout.dropdown_item,
                availableLocales
        );
        System.out.println("For position " + position + " locales: " + availableLocales);
        adapter.setDropDownViewResource(R.layout.dropdown_item);

        LanguageItem languageItem = data.get(position);
        holder.languagesDropdown.setAdapter(adapter);

        if (position == 0) {
            holder.removeButton.setVisibility(View.GONE);
            holder.languagesDropdownParent.setVisibility(View.GONE);
            holder.defaultLanguageLabel.setVisibility(View.VISIBLE);
        } else {
            holder.removeButton.setVisibility(View.VISIBLE);
            holder.defaultLanguageLabel.setVisibility(View.GONE);
            holder.languagesDropdownParent.setVisibility(View.VISIBLE);
        }

        holder.bindToLanguageItem(languageItem);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty() || !(payloads.get(0) instanceof LocaleRefreshPayload payload)) {
            super.onBindViewHolder(holder, position, payloads);
            return;
        }

        System.out.println("Updating " + position + " with payload");

        ArrayAdapter<SpannableString> adapter = new ArrayAdapter<>(
                holder.itemView.getContext(),
                R.layout.dropdown_item,
                payload.availableLocales()
        );

        adapter.setDropDownViewResource(R.layout.dropdown_item);
        holder.languagesDropdown.setAdapter(adapter);
        String currentLocale = data.get(position).getLocale();
        if (currentLocale != null) {
            if (currentLocale.contains("[")) {
                System.out.println("Spannable!");
                holder.languagesDropdown.setText(getCustomFlag(currentLocale), false);
            } else {
                holder.languagesDropdown.setText(currentLocale, false);
            }
        } else {
            holder.languagesDropdown.setText(null, false);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder {
        private final AutoCompleteTextView languagesDropdown;
        private final TextInputLayout languagesDropdownParent;
        private final MaterialTextView defaultLanguageLabel;
        private final TextInputEditText nameInput;
        private final MaterialButton removeButton;
        private TextWatcher textWatcher;
        private final Function<String, SpannableString> customFlagFactory;

        public interface onRemoveClickListener {
            void onRemove(int position);
        }

        public interface onLocaleSelectedCallback {
            void onSelect(int langItemPosition, String selectedLocale);
        }

        public viewholder(@NonNull View itemView,
                          onRemoveClickListener removeClickListener,
                          onLocaleSelectedCallback localeSelectedCallback,
                          Function<String, SpannableString> customFlagFactory) {
            super(itemView);

            this.languagesDropdownParent = itemView.findViewById(R.id.editor_language_selection);
            this.defaultLanguageLabel = itemView.findViewById(R.id.editor_default_language_label);
            this.languagesDropdown = itemView.findViewById(R.id.editor_language_selection_dropdown);
            this.nameInput = itemView.findViewById(R.id.editor_localized_name_input);
            this.removeButton = itemView.findViewById(R.id.editor_remove_name_button);
            this.customFlagFactory = customFlagFactory;

            removeButton.setOnClickListener(view -> {
                if (getBindingAdapterPosition() == RecyclerView.NO_POSITION) return;
                languagesDropdown.clearFocus();
                clearFocusAndHideKeyboard(nameInput);
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    removeClickListener.onRemove(position);
                }
            });

            languagesDropdown.setOnItemClickListener((adapterView, view, i, l) -> {
                if (getBindingAdapterPosition() == RecyclerView.NO_POSITION) return;
                languagesDropdown.clearFocus();
                clearFocusAndHideKeyboard(nameInput);
                localeSelectedCallback.onSelect(getBindingAdapterPosition(), languagesDropdown.getText().toString());
            });

            nameInput.setOnEditorActionListener(((textView, i, keyEvent) -> {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    clearFocusAndHideKeyboard(nameInput);
                    return true;
                }
                return false;
            }));
        }

        public void bindToLanguageItem(LanguageItem item) {
            if (textWatcher != null) {
                nameInput.removeTextChangedListener(textWatcher);
            }

            nameInput.setText(item.getName() != null ? item.getName() : "");
            if (languagesDropdownParent.getVisibility() == View.VISIBLE) {
                String locale = item.getLocale();
                if (locale != null) {
                    if (locale.contains("[")) {
                        System.out.println("Spannable!");
                        languagesDropdown.setText(customFlagFactory.apply(locale), false);
                    } else {
                        languagesDropdown.setText(locale, false);
                    }
                } else {
                    languagesDropdown.setText(null, false);
                }
            } else {
                defaultLanguageLabel.setText(item.getLocale());
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

        private void clearFocusAndHideKeyboard(TextInputEditText input) {
            input.clearFocus();

            InputMethodManager inputMethodManager = (InputMethodManager)
                    input.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
            }
        }
    }
}
