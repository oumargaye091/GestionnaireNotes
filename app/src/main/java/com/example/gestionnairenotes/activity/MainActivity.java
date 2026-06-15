package com.example.gestionnairenotes.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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
    private TextView textAucuneNote, textCompteur;
    private EditText editRecherche;
    private Button btnFavoris, btnTri, btnTheme;
    private FloatingActionButton fabAjouter;
    private boolean filtreActif = false;

    // BONUS - Tri
    private String[] tris = {"date", "titre", "couleur"};
    private String[] trisLabels = {"Date", "Titre", "Couleur"};
    private int triIndex = 0;

    private static final String PREFS = "prefs_app";
    private static final String KEY_THEME = "mode_sombre";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // BONUS - Mode sombre (à appliquer avant setContentView)
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        boolean sombre = prefs.getBoolean(KEY_THEME, false);
        AppCompatDelegate.setDefaultNightMode(
                sombre ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerNotes   = findViewById(R.id.recyclerNotes);
        textAucuneNote  = findViewById(R.id.textAucuneNote);
        textCompteur    = findViewById(R.id.textCompteur);
        editRecherche   = findViewById(R.id.editRecherche);
        btnFavoris      = findViewById(R.id.btnFavoris);
        btnTri          = findViewById(R.id.btnTri);
        btnTheme        = findViewById(R.id.btnTheme);
        fabAjouter      = findViewById(R.id.fabAjouter);

        noteDAO = new NoteDAO(this);
        recyclerNotes.setLayoutManager(new LinearLayoutManager(this));
        btnTheme.setText(sombre ? "☀️" : "🌙");
        chargerNotes();

        // Recherche
        editRecherche.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            public void onTextChanged(CharSequence s, int a, int b, int c) {
                rechercherNotes(s.toString());
            }
            public void afterTextChanged(Editable s) {}
        });

        // Favoris
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

        // BONUS - Tri (cycle date → titre → couleur)
        btnTri.setOnClickListener(v -> {
            triIndex = (triIndex + 1) % tris.length;
            btnTri.setText("Tri: " + trisLabels[triIndex]);
            if (filtreActif) afficherFavoris(); else chargerNotes();
        });

        // BONUS - Mode sombre
        btnTheme.setOnClickListener(v -> {
            boolean nouveau = !prefs.getBoolean(KEY_THEME, false);
            prefs.edit().putBoolean(KEY_THEME, nouveau).apply();
            AppCompatDelegate.setDefaultNightMode(
                    nouveau ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            recreate();
        });

        fabAjouter.setOnClickListener(v -> afficherPalette());

        findViewById(R.id.btnVert).setOnClickListener(v   -> { masquerPalette(); ouvrirCreation("#219653"); });
        findViewById(R.id.btnRouge).setOnClickListener(v  -> { masquerPalette(); ouvrirCreation("#EB5757"); });
        findViewById(R.id.btnBleu).setOnClickListener(v   -> { masquerPalette(); ouvrirCreation("#2F80ED"); });
        findViewById(R.id.btnJaune).setOnClickListener(v  -> { masquerPalette(); ouvrirCreation("#F2C94C"); });
        findViewById(R.id.btnOrange).setOnClickListener(v -> { masquerPalette(); ouvrirCreation("#F2994A"); });
        findViewById(R.id.btnGris).setOnClickListener(v   -> { masquerPalette(); ouvrirCreation("#828282"); });
    }

    private void chargerNotes() {
        afficherListe(noteDAO.getNotesTriees(tris[triIndex]));
    }

    private void afficherFavoris() {
        afficherListe(noteDAO.getFavoris());
    }

    private void rechercherNotes(String texte) {
        afficherListe(noteDAO.rechercherParTitre(texte));
    }

    private void afficherListe(List<Note> notes) {
        // BONUS - Compteur (toujours total)
        int total = noteDAO.compterNotes();
        textCompteur.setText(total <= 1 ? total + " note" : total + " notes");

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
        palette.setVisibility(palette.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    private void masquerPalette() {
        findViewById(R.id.layoutPalette).setVisibility(View.GONE);
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
        if (filtreActif) afficherFavoris(); else chargerNotes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (filtreActif) afficherFavoris(); else chargerNotes();
    }
}
