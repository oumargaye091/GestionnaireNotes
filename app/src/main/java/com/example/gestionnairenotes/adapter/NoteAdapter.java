package com.example.gestionnairenotes.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionnairenotes.R;
import com.example.gestionnairenotes.model.Note;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private Context context;
    private List<Note> notes;
    private OnNoteClickListener listener;

    // Interface pour les clics
    public interface OnNoteClickListener {
        void onNoteClick(Note note);
        void onNoteDoubleClick(Note note);
    }

    public NoteAdapter(Context context, List<Note> notes, OnNoteClickListener listener) {
        this.context = context;
        this.notes = notes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);

        // Titre et date
        holder.textTitre.setText(note.getTitre());
        holder.textDate.setText(note.getDate());

        // Couleur de fond
        holder.layoutNote.setBackgroundColor(Color.parseColor(note.getCouleur()));

        // Icône favori
        if (note.isFavori()) {
            holder.imageFavori.setVisibility(View.VISIBLE);
        } else {
            holder.imageFavori.setVisibility(View.GONE);
        }

        // Clic simple → modification
        holder.itemView.setOnClickListener(v -> listener.onNoteClick(note));

        // Double clic → favori
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            private int nbClics = 0;
            private final long DELAI = 300;

            @Override
            public void onClick(View v) {
                nbClics++;
                v.postDelayed(() -> {
                    if (nbClics == 1) {
                        listener.onNoteClick(note);
                    } else if (nbClics >= 2) {
                        listener.onNoteDoubleClick(note);
                    }
                    nbClics = 0;
                }, DELAI);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    // Mettre à jour la liste
    public void mettreAJourListe(List<Note> nouvellesNotes) {
        this.notes = nouvellesNotes;
        notifyDataSetChanged();
    }

    // ViewHolder
    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        View layoutNote;
        TextView textTitre;
        TextView textDate;
        ImageView imageFavori;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutNote = itemView.findViewById(R.id.layoutNote);
            textTitre = itemView.findViewById(R.id.textTitre);
            textDate = itemView.findViewById(R.id.textDate);
            imageFavori = itemView.findViewById(R.id.imageFavori);
        }
    }
}