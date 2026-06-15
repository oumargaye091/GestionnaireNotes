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

    // CREATE - Ajouter une note
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

    // READ - Récupérer toutes les notes
    public List<Note> getToutesLesNotes() {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_NOTES,
                null, null, null, null, null,
                DatabaseHelper.COLONNE_DATE + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                notes.add(cursorVersNote(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return notes;
    }

    // READ - Récupérer uniquement les favoris
    public List<Note> getFavoris() {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_NOTES,
                null,
                DatabaseHelper.COLONNE_FAVORI + " = ?",
                new String[]{"1"},
                null, null,
                DatabaseHelper.COLONNE_DATE + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                notes.add(cursorVersNote(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return notes;
    }

    // READ - Rechercher par titre
    public List<Note> rechercherParTitre(String recherche) {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_NOTES,
                null,
                DatabaseHelper.COLONNE_TITRE + " LIKE ?",
                new String[]{"%" + recherche + "%"},
                null, null,
                DatabaseHelper.COLONNE_DATE + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                notes.add(cursorVersNote(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return notes;
    }

    // UPDATE - Modifier une note
    public int modifierNote(Note note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLONNE_TITRE, note.getTitre());
        values.put(DatabaseHelper.COLONNE_CONTENU, note.getContenu());
        values.put(DatabaseHelper.COLONNE_COULEUR, note.getCouleur());
        values.put(DatabaseHelper.COLONNE_FAVORI, note.isFavori() ? 1 : 0);
        values.put(DatabaseHelper.COLONNE_DATE, note.getDate());

        int lignesMaj = db.update(
                DatabaseHelper.TABLE_NOTES,
                values,
                DatabaseHelper.COLONNE_ID + " = ?",
                new String[]{String.valueOf(note.getId())}
        );

        db.close();
        return lignesMaj;
    }

    // UPDATE - Basculer favori
    public void basculerFavori(Note note) {
        note.setFavori(!note.isFavori());
        modifierNote(note);
    }

    // DELETE - Supprimer une note
    public void supprimerNote(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(
                DatabaseHelper.TABLE_NOTES,
                DatabaseHelper.COLONNE_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        db.close();
    }

    // Utilitaire - Convertir un cursor en Note
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