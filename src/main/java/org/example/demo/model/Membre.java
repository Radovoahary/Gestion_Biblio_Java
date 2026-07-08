package org.example.demo.model;

public class Membre {
    private int id;
    private String nom;
    private String email;
    private String typeMembre; // stocke 'ETUDIANT' ou 'ENSEIGNANT'

    // Constructeur
    public Membre(int id, String nom, String email, String typeMembre) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.typeMembre = typeMembre;
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

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getTypeMembre() {
        return typeMembre;
    }
    public void setTypeMembre(String typeMembre) {
        this.typeMembre = typeMembre;
    }
}