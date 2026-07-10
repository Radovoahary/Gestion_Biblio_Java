package org.example.demo.model;

public class Livre {

    private int id;
    private String titre;
    private String auteur;
    private String isbn;
    private int annee;
    private int exemplairesDisponibles;
    private int categorieId;

    public Livre() {
    }

    public Livre(int id, String titre, String auteur, String isbn, int annee,
                 int exemplairesDisponibles, int categorieId) {
        this.id = id;
        this.titre = titre;
        this.auteur = auteur;
        this.isbn = isbn;
        this.annee = annee;
        this.exemplairesDisponibles = exemplairesDisponibles;
        this.categorieId = categorieId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public int getExemplairesDisponibles() {
        return exemplairesDisponibles;
    }

    public void setExemplairesDisponibles(int exemplairesDisponibles) {
        this.exemplairesDisponibles = exemplairesDisponibles;
    }

    public int getCategorieId() {
        return categorieId;
    }

    public void setCategorieId(int categorieId) {
        this.categorieId = categorieId;
    }

    /** Amélioration : pratique pour le débogage / les logs. */
    @Override
    public String toString() {
        return "Livre{id=" + id + ", titre='" + titre + "', auteur='" + auteur +
                "', isbn='" + isbn + "', annee=" + annee +
                ", exemplairesDisponibles=" + exemplairesDisponibles +
                ", categorieId=" + categorieId + '}';
    }
}
