package com.example.gestionnairenotes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionnairenotes.R;
import com.example.gestionnairenotes.adapter.NoteAdapter;
import com.example.gestionnairenotes.database.NoteDAO;
import com.example.gestionnairenotes.model.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteAdapter.OnNoteClickListener {

    private RecyclerView recyclerNotes;
    private NoteAdapter adapter;
    private NoteDAO noteDAO;
    private TextView textAucuneNote;
    private EditText editRecherche;
    private Button btnFavoris;
    private FloatingActionButton fabAjouter;
    private boolean filtreActif = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerNotes = findViewById(R.id.recyclerNotes);
        textAucuneNote = findViewById(R.id.textAucuneNote);
        editRecherche = findViewById(R.id.editRecherche);
        btnFavoris = findViewById(R.id.btnFavoris);
        fabAjouter = findViewById(R.id.fabAjouter);

        noteDAO = new NoteDAO(this);
        recyclerNotes.setLayoutManager(new LinearLayoutManager(this));
        chargerNotes();

        // Recherche en temps réel
        editRecherche.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                rechercherNotes(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Bouton Favoris
        btnFavoris.setOnClickListener(v -> {
            filtreActif = !filtreActif;
            if (filtreActif) {
                btnFavoris.setTextColor(getResources().getColor(R.color.blanc));
                btnFavoris.setBackgroundResource(R.drawable.bg_btn_favoris_actif);
                afficherFavoris();
            } else {
                btnFavoris.setTextColor(getResources().getColor(R.color.noir));
                btnFavoris.setBackgroundResource(R.drawable.bg_btn_favoris);
                chargerNotes();
            }
        });

        // Bouton flottant
        fabAjouter.setOnClickListener(v -> afficherPalette());

        // Clics sur les couleurs
        findViewById(R.id.btnVert).setOnClickListener(v -> {
            findViewById(R.id.layoutPalette).setVisibility(View.GONE);
            ouvrirCreation("#219653");
        });
        findViewById(R.id.btnRouge).setOnClickListener(v -> {
            findViewById(R.id.layoutPalette).setVisibility(View.GONE);
            ouvrirCreation("#EB5757");
        });
        findViewById(R.id.btnBleu).setOnClickListener(v -> {
            findViewById(R.id.layoutPalette).setVisibility(View.GONE);
            ouvrirCreation("#2F80ED");
        });
        findViewById(R.id.btnJaune).setOnClickListener(v -> {
            findViewById(R.id.layoutPalette).setVisibility(View.GONE);
            ouvrirCreation("#F2C94C");
        });
        findViewById(R.id.btnOrange).setOnClickListener(v -> {
            findViewById(R.id.layoutPalette).setVisibility(View.GONE);
            ouvrirCreation("#F2994A");
        });
        findViewById(R.id.btnGris).setOnClickListener(v -> {
            findViewById(R.id.layoutPalette).setVisibility(View.GONE);
            ouvrirCreation("#828282");
        });
    }

    private void chargerNotes() {
        List<Note> notes = noteDAO.getToutesLesNotes();
        afficherListe(notes);
    }

    private void afficherFavoris() {
        List<Note> favoris = noteDAO.getFavoris();
        afficherListe(favoris);
    }

    private void rechercherNotes(String texte) {
        List<Note> notes = noteDAO.rechercherParTitre(texte);
        afficherListe(notes);
    }

    private void afficherListe(List<Note> notes) {
        if (notes.isEmpty()) {
            recyclerNotes.setVisibility(View.GONE);
            textAucuneNote.setVisibility(View.VISIBLE);
        } else {
            recyclerNotes.setVisibility(View.VISIBLE);
            textAucuneNote.setVisibility(View.GONE);
        }
        if (adapter == null) {
            adapter = new NoteAdapter(this, notes, this);
            recyclerNotes.setAdapter(adapter);
        } else {
            adapter.mettreAJourListe(notes);
        }
    }

    private void afficherPalette() {
        View palette = findViewById(R.id.layoutPalette);
        if (palette.getVisibility() == View.VISIBLE) {
            palette.setVisibility(View.GONE);
        } else {
            palette.setVisibility(View.VISIBLE);
        }
    }

    private void ouvrirCreation(String couleur) {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra("couleur", couleur);
        startActivity(intent);
    }

    @Override
    public void onNoteClick(Note note) {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra("note_id", note.getId());
        intent.putExtra("couleur", note.getCouleur());
        startActivity(intent);
    }

    @Override
    public void onNoteDoubleClick(Note note) {
        noteDAO.basculerFavori(note);
        if (filtreActif) {
            afficherFavoris();
        } else {
            chargerNotes();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (filtreActif) {
            afficherFavoris();
        } else {
            chargerNotes();
        }
    }
}