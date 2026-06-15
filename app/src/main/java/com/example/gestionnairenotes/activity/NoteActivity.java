package com.example.gestionnairenotes.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gestionnairenotes.R;
import com.example.gestionnairenotes.database.NoteDAO;
import com.example.gestionnairenotes.model.Note;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteActivity extends AppCompatActivity {

    private EditText editTitre, editContenu;
    private Button btnSauvegarder, btnSupprimer, btnPartager;
    private LinearLayout layoutNote;
    private NoteDAO noteDAO;

    private String couleur;
    private int noteId = -1;
    private boolean favoriActuel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        editTitre       = findViewById(R.id.editTitre);
        editContenu     = findViewById(R.id.editContenu);
        btnSauvegarder  = findViewById(R.id.btnSauvegarder);
        btnSupprimer    = findViewById(R.id.btnSupprimer);
        btnPartager     = findViewById(R.id.btnPartager);
        layoutNote      = findViewById(R.id.layoutNote);

        noteDAO = new NoteDAO(this);

        couleur = getIntent().getStringExtra("couleur");
        noteId  = getIntent().getIntExtra("note_id", -1);

        if (couleur == null || couleur.isEmpty()) couleur = "#828282";
        appliquerCouleur(couleur);

        if (noteId != -1) {
            btnSauvegarder.setText("Modifier");
            btnSupprimer.setVisibility(View.VISIBLE); // BONUS visible en édition
            chargerNote(noteId);
        } else {
            btnSauvegarder.setText("Créer");
        }

        configurerPalette();

        btnSauvegarder.setOnClickListener(v -> sauvegarder());

        // BONUS - Suppression avec confirmation
        btnSupprimer.setOnClickListener(v -> confirmerSuppression());

        // BONUS - Partage
        btnPartager.setOnClickListener(v -> partager());
    }

    private void configurerPalette() {
        findViewById(R.id.couleurVert).setOnClickListener(v   -> appliquerCouleur("#219653"));
        findViewById(R.id.couleurRouge).setOnClickListener(v  -> appliquerCouleur("#EB5757"));
        findViewById(R.id.couleurBleu).setOnClickListener(v   -> appliquerCouleur("#2F80ED"));
        findViewById(R.id.couleurJaune).setOnClickListener(v  -> appliquerCouleur("#F2C94C"));
        findViewById(R.id.couleurOrange).setOnClickListener(v -> appliquerCouleur("#F2994A"));
        findViewById(R.id.couleurGris).setOnClickListener(v   -> appliquerCouleur("#828282"));
    }

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
                favoriActuel = note.isFavori();
                appliquerCouleur(note.getCouleur());
                break;
            }
        }
    }

    private void sauvegarder() {
        String titre = editTitre.getText().toString().trim();
        String contenu = editContenu.getText().toString().trim();

        if (titre.isEmpty()) {
            Toast.makeText(this, "Le titre est obligatoire", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = new SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH).format(new Date());

        if (noteId == -1) {
            noteDAO.ajouterNote(new Note(titre, contenu, couleur, false, date));
            Toast.makeText(this, "Note créée !", Toast.LENGTH_SHORT).show();
        } else {
            noteDAO.modifierNote(new Note(noteId, titre, contenu, couleur, favoriActuel, date));
            Toast.makeText(this, "Note modifiée !", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    // BONUS - Suppression
    private void confirmerSuppression() {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer la note")
                .setMessage("Êtes-vous sûr ?")
                .setPositiveButton("Supprimer", (d, w) -> {
                    noteDAO.supprimerNote(noteId);
                    Toast.makeText(this, "Note supprimée", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    // BONUS - Partage
    private void partager() {
        String titre = editTitre.getText().toString().trim();
        String contenu = editContenu.getText().toString().trim();
        if (titre.isEmpty() && contenu.isEmpty()) {
            Toast.makeText(this, "Rien à partager", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, titre);
        intent.putExtra(Intent.EXTRA_TEXT, titre + "\n\n" + contenu);
        startActivity(Intent.createChooser(intent, "Partager la note"));
    }
}
