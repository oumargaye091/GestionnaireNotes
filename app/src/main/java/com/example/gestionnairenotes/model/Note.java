package com.example.gestionnairenotes.model;

public class Note {

    private int id;
    private String titre;
    private String contenu;
    private String couleur;
    private boolean favori;
    private String date;

    // Constructeur sans id (pour la création)
    public Note(String titre, String contenu, String couleur, boolean favori, String date) {
        this.titre = titre;
        this.contenu = contenu;
        this.couleur = couleur;
        this.favori = favori;
        this.date = date;
    }

    // Constructeur avec id (pour la lecture depuis la base)
    public Note(int id, String titre, String contenu, String couleur, boolean favori, String date) {
        this.id = id;
        this.titre = titre;
        this.contenu = contenu;
        this.couleur = couleur;
        this.favori = favori;
        this.date = date;
    }

    // Getters
    public int getId() { return id; }
    public String getTitre() { return titre; }
    public String getContenu() { return contenu; }
    public String getCouleur() { return couleur; }
    public boolean isFavori() { return favori; }
    public String getDate() { return date; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTitre(String titre) { this.titre = titre; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public void setCouleur(String couleur) { this.couleur = couleur; }
    public void setFavori(boolean favori) { this.favori = favori; }
    public void setDate(String date) { this.date = date; }
}