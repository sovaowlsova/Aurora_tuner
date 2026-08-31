package com.sovaowlsova.auroratuner.tuner.ui.instruments;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sovaowlsova.auroratuner.R;
import com.sovaowlsova.auroratuner.core.data.Note;
import com.sovaowlsova.auroratuner.core.model.Tuning;
import com.sovaowlsova.auroratuner.tuner.ui.TunerNoteInfo;

public class GeneratedNotesRVAdapter extends RecyclerView.Adapter<GeneratedNotesRVAdapter.viewholder> {
    private Tuning tuning;

    public GeneratedNotesRVAdapter(Tuning tuning) {
        this.tuning = tuning;
    }

    public void setNote(TunerNoteInfo info) {
        Note closestString = tuning.getClosestString(info.frequency());
        int firstIndexOfNote = tuning.getNotes().indexOf(closestString);
    }

    public void setTuning(Tuning tuning) {
        this.tuning = tuning;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.generated_note_item, parent, false);
        return new viewholder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        holder.noteNumberText.setText(String.valueOf(position + 1));
        holder.noteText.setText(tuning.getNotes().get(position).getName());
    }

    @Override
    public int getItemCount() {
        return tuning.getNotes().size();
    }

    public static class viewholder extends RecyclerView.ViewHolder {
        private final TextView noteText;
        private final TextView noteNumberText;

        public viewholder(@NonNull View itemView) {
            super(itemView);

            this.noteText = itemView.findViewById(R.id.note_text);
            this.noteNumberText = itemView.findViewById(R.id.note_number_text);
        }

        public void highlightNote(boolean inTune) {
            int newTextColor = inTune ? Color.GREEN : Color.YELLOW;
            noteText.setTextColor(newTextColor);
        }

        public void unhighlightNote() {
            noteText.setTextColor(Color.WHITE);
        }
    }
}
