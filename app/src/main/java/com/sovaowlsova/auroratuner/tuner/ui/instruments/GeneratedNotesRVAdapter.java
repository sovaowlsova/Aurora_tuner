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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GeneratedNotesRVAdapter extends RecyclerView.Adapter<GeneratedNotesRVAdapter.viewholder> {
    private Tuning tuning;
    private final Set<Integer> highlightedNotes = new HashSet<>();
    private final Set<Integer> tunedNotes = new HashSet<>();

    public GeneratedNotesRVAdapter(Tuning tuning) {
        this.tuning = tuning;
    }

    public void setNote(TunerNoteInfo info) {
        Note closestString = tuning.getClosestString(info.frequency());
        Set<Integer> affectedStrings = new HashSet<>();
        List<Note> notes = tuning.getNotes();
        for (int i = 0; i < notes.size(); i++) {
            if (tunedNotes.contains(i)) continue;
            if (notes.get(i).equals(closestString)) {
                affectedStrings.add(i);
            }
        }

        if (info.inTune() && closestString.equals(info.note())) {
            tunedNotes.addAll(affectedStrings);
            Set<Integer> temp = Set.copyOf(highlightedNotes);
            highlightedNotes.clear();
            affectedStrings.addAll(temp);
        } else {
            Set<Integer> temp = Set.copyOf(highlightedNotes);
            highlightedNotes.clear();
            highlightedNotes.addAll(affectedStrings);
            affectedStrings.addAll(temp);
        }

        System.out.println(affectedStrings);
        affectedStrings.forEach(this::notifyItemChanged);
    }

    public void setTuning(Tuning tuning) {
        this.tuning = tuning;
        resetUI();
    }

    public void resetUI() {
        highlightedNotes.clear();
        tunedNotes.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.generated_note_item, parent, false);
        return new viewholder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        System.out.println("Binding view for " + position);
        if (highlightedNotes.contains(position)) {
            System.out.println("Highlighting");
            holder.noteText.setTextColor(Color.YELLOW);
        } else if (tunedNotes.contains(position)) {
            System.out.println("Tuning");
            holder.noteText.setTextColor(Color.GREEN);
        } else {
            System.out.println("Dehighlighting");
            holder.noteText.setTextColor(Color.WHITE);
        }
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
