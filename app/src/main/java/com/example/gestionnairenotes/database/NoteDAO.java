package com.example.gestionnairenotes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gestionnairenotes.model.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteDAO {

    private DatabaseHelper dbHelper;

    public NoteDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long ajouterNote(Note note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLONNE_TITRE, note.getTitre());
        values.put(DatabaseHelper.COLONNE_CONTENU, note.getContenu());
        values.put(DatabaseHelper.COLONNE_COULEUR, note.getCouleur());
        values.put(DatabaseHelper.COLONNE_FAVORI, note.isFavori() ? 1 : 0);
        values.put(DatabaseHelper.COLONNE_DATE, note.getDate());
        long id = db.insert(DatabaseHelper.TABLE_NOTES, null, values);
        db.close();
        return id;
    }

    public List<Note> getToutesLesNotes() {
        return getNotesTriees("date");
    }

    // Tri : "date" (défaut), "titre", "couleur"
    public List<Note> getNotesTriees(String critere) {
        String orderBy;
        if ("titre".equals(critere)) {
            orderBy = DatabaseHelper.COLONNE_TITRE + " COLLATE NOCASE ASC";
        } else if ("couleur".equals(critere)) {
            orderBy = DatabaseHelper.COLONNE_COULEUR + " ASC";
        } else {
            orderBy = DatabaseHelper.COLONNE_ID + " DESC";
        }

        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NOTES,
                null, null, null, null, null, orderBy);
        if (cursor.moveToFirst()) {
            do { notes.add(cursorVersNote(cursor)); } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notes;
    }

    public List<Note> getFavoris() {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NOTES, null,
                DatabaseHelper.COLONNE_FAVORI + " = ?", new String[]{"1"},
                null, null, DatabaseHelper.COLONNE_ID + " DESC");
        if (cursor.moveToFirst()) {
            do { notes.add(cursorVersNote(cursor)); } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notes;
    }

    public List<Note> rechercherParTitre(String recherche) {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NOTES, null,
                DatabaseHelper.COLONNE_TITRE + " LIKE ?",
                new String[]{"%" + recherche + "%"},
                null, null, DatabaseHelper.COLONNE_ID + " DESC");
        if (cursor.moveToFirst()) {
            do { notes.add(cursorVersNote(cursor)); } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notes;
    }

    public int modifierNote(Note note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLONNE_TITRE, note.getTitre());
        values.put(DatabaseHelper.COLONNE_CONTENU, note.getContenu());
        values.put(DatabaseHelper.COLONNE_COULEUR, note.getCouleur());
        values.put(DatabaseHelper.COLONNE_FAVORI, note.isFavori() ? 1 : 0);
        values.put(DatabaseHelper.COLONNE_DATE, note.getDate());
        int lignesMaj = db.update(DatabaseHelper.TABLE_NOTES, values,
                DatabaseHelper.COLONNE_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
        return lignesMaj;
    }

    public void basculerFavori(Note note) {
        note.setFavori(!note.isFavori());
        modifierNote(note);
    }

    // BONUS - Suppression
    public void supprimerNote(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_NOTES,
                DatabaseHelper.COLONNE_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    // BONUS - Compteur
    public int compterNotes() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_NOTES, null);
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    private Note cursorVersNote(Cursor cursor) {
        return new Note(
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLONNE_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLONNE_TITRE)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLONNE_CONTENU)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLONNE_COULEUR)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLONNE_FAVORI)) == 1,
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLONNE_DATE))
        );
    }
}
