package com.sovaowlsova.auroratuner.editor.model;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.sovaowlsova.auroratuner.R;

import java.util.ArrayList;
import java.util.List;

public class NotesRVAdapter extends RecyclerView.Adapter<NotesRVAdapter.viewholder> {
    private final List<String> data = new ArrayList<>();
    private final Context context;

    private MutableLiveData<Integer> dataSizeData;

    public NotesRVAdapter(Context context) {
        this.context = context;
        addNote();
    }

    public LiveData<Integer> getDataSizeData() {
        if (dataSizeData == null) {
            dataSizeData = new MutableLiveData<>();
        }
        return dataSizeData;
    }

    public ArrayList<String> getData() {
        return new ArrayList<>(data);
    }

    public void addNote() {
        data.add("");
        notifyItemInserted(data.size() - 1);
        if (dataSizeData == null) getDataSizeData();
        dataSizeData.postValue(getItemCount());
    }

    protected void removeNote(int position) {
        data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, data.size() - position);
        dataSizeData.postValue(getItemCount());
    }

    private void changeData(int position, String newData) {
        data.set(position, newData);
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.editor_note_item, parent, false);
        return new viewholder(item, this::removeNote, this::changeData);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        holder.noteInput.setText(data.get(position));
        holder.noteNumber.setText(String.valueOf(position + 1));

        holder.removeButton.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder {
        private final TextInputEditText noteInput;
        private final TextView noteNumber;
        private final MaterialButton removeButton;
        private TextWatcher textWatcher;
        private final OnDataChangedCallback onDataChangedCallback;

        public interface onRemoveClickListener {
            void onRemove(int position);
        }

        public interface OnDataChangedCallback {
            void onChanged(int position, String newData);
        }

        public viewholder(@NonNull View itemView, onRemoveClickListener removeClickListener, OnDataChangedCallback onDataChangedCallback) {
            super(itemView);

            this.noteInput = itemView.findViewById(R.id.editor_note_input);
            this.noteNumber = itemView.findViewById(R.id.editor_note_number_text);
            this.removeButton = itemView.findViewById(R.id.editor_remove_note_button);

            removeButton.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return;
                removeClickListener.onRemove(position);
            });

            noteInput.setOnEditorActionListener(((textView, i, keyEvent) -> {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    clearFocusAndHideKeyboard(noteInput);
                    return true;
                }
                return false;
            }));

            this.onDataChangedCallback = onDataChangedCallback;

            configureTextWatcher();
        }

        private void configureTextWatcher() {
            if (textWatcher != null) {
                noteInput.removeTextChangedListener(textWatcher);
            }

            textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    int position = getBindingAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) return;
                    onDataChangedCallback.onChanged(position, charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            };

            noteInput.addTextChangedListener(textWatcher);
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
