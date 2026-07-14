package org.example.demo.model;

public class Categorie {

    private int id;
    private String nom;
    private String description;

    public Categorie() {
    }

    public Categorie(int id, String nom, String description) {
        this.id = id;
        this.nom = nom;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Important : utilisé par la ComboBox du formulaire Livres pour afficher
     * le nom de la catégorie au lieu de la référence mémoire de l'objet.
     */
    @Override
    public String toString() {
        return nom;
    }
}
