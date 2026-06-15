package com.example.gestionnairenotes.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gestionnairenotes.R;
import com.example.gestionnairenotes.database.NoteDAO;
import com.example.gestionnairenotes.model.Note;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteActivity extends AppCompatActivity {

    private EditText editTitre;
    private EditText editContenu;
    private Button btnSauvegarder;
    private LinearLayout layoutNote;
    private NoteDAO noteDAO;

    private String couleur;
    private int noteId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        editTitre = findViewById(R.id.editTitre);
        editContenu = findViewById(R.id.editContenu);
        btnSauvegarder = findViewById(R.id.btnSauvegarder);
        layoutNote = findViewById(R.id.layoutNote);

        noteDAO = new NoteDAO(this);

        couleur = getIntent().getStringExtra("couleur");
        noteId = getIntent().getIntExtra("note_id", -1);

        if (couleur == null || couleur.isEmpty()) {
            couleur = "#828282";
        }

        appliquerCouleur(couleur);

        if (noteId != -1) {
            btnSauvegarder.setText("Modifier");
            chargerNote(noteId);
        } else {
            btnSauvegarder.setText("Créer");
        }

        // 🎨 NOUVEAU : palette de couleurs
        configurerPalette();

        btnSauvegarder.setOnClickListener(v -> sauvegarder());
    }

    // 🎨 NOUVEAU : clics sur la palette
    private void configurerPalette() {
        View vert   = findViewById(R.id.couleurVert);
        View rouge  = findViewById(R.id.couleurRouge);
        View bleu   = findViewById(R.id.couleurBleu);
        View jaune  = findViewById(R.id.couleurJaune);
        View orange = findViewById(R.id.couleurOrange);
        View gris   = findViewById(R.id.couleurGris);

        vert.setOnClickListener(v   -> appliquerCouleur("#219653"));
        rouge.setOnClickListener(v  -> appliquerCouleur("#EB5757"));
        bleu.setOnClickListener(v   -> appliquerCouleur("#2F80ED"));
        jaune.setOnClickListener(v  -> appliquerCouleur("#F2C94C"));
        orange.setOnClickListener(v -> appliquerCouleur("#F2994A"));
        gris.setOnClickListener(v   -> appliquerCouleur("#828282"));
    }

    // 🎨 NOUVEAU : applique + mémorise la couleur
    private void appliquerCouleur(String hex) {
        try {
            layoutNote.setBackgroundColor(Color.parseColor(hex));
            couleur = hex;
        } catch (IllegalArgumentException e) {
            layoutNote.setBackgroundColor(Color.parseColor("#828282"));
            couleur = "#828282";
        }
    }

    private void chargerNote(int id) {
        for (Note note : noteDAO.getToutesLesNotes()) {
            if (note.getId() == id) {
                editTitre.setText(note.getTitre());
                editContenu.setText(note.getContenu());
                appliquerCouleur(note.getCouleur());
                break;
            }
        }
    }

    private void sauvegarder() {
        String titre = editTitre.getText().toString().trim();
        String contenu = editContenu.getText().toString().trim();

        if (titre.isEmpty() || contenu.isEmpty()) {
            Toast.makeText(this, "Le titre et le contenu sont obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = new SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH).format(new Date());

        if (noteId == -1) {
            Note note = new Note(titre, contenu, couleur, false, date);
            noteDAO.ajouterNote(note);
            Toast.makeText(this, "Note créée !", Toast.LENGTH_SHORT).show();
        } else {
            Note note = new Note(noteId, titre, contenu, couleur, false, date);
            noteDAO.modifierNote(note);
            Toast.makeText(this, "Note modifiée !", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
