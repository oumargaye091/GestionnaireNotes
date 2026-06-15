package com.example.gestionnairenotes.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Informations de la base
    private static final String NOM_BASE = "gestionnaire_notes.db";
    private static final int VERSION = 1;

    // Table et colonnes
    public static final String TABLE_NOTES = "notes";
    public static final String COLONNE_ID = "id";
    public static final String COLONNE_TITRE = "titre";
    public static final String COLONNE_CONTENU = "contenu";
    public static final String COLONNE_COULEUR = "couleur";
    public static final String COLONNE_FAVORI = "favori";
    public static final String COLONNE_DATE = "date";

    // Requête de création de la table
    private static final String CREER_TABLE =
            "CREATE TABLE " + TABLE_NOTES + " (" +
                    COLONNE_ID      + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLONNE_TITRE   + " TEXT NOT NULL, " +
                    COLONNE_CONTENU + " TEXT NOT NULL, " +
                    COLONNE_COULEUR + " TEXT NOT NULL, " +
                    COLONNE_FAVORI  + " INTEGER DEFAULT 0, " +
                    COLONNE_DATE    + " TEXT NOT NULL" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, NOM_BASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int ancienneVersion, int nouvelleVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }
}