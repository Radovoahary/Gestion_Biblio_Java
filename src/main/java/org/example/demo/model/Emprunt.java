package org.example.demo.model;

import java.time.LocalDate;

public class Emprunt {
    private int id;
    private int idLivre;
    private int idMembre;
    private LocalDate dateEmprunt;
    private LocalDate dateRetourPrevue;
    private LocalDate dateRetourEffective;

    // Attributs bonus
    private String titreLivre;
    private String nomMembre;

    // Constructeur
    public Emprunt(int id, int idLivre, int idMembre, LocalDate dateEmprunt, LocalDate dateRetourPrevue, LocalDate dateRetourEffective) {
        this.id = id;
        this.idLivre = idLivre;
        this.idMembre = idMembre;
        this.dateEmprunt = dateEmprunt;
        this.dateRetourPrevue = dateRetourPrevue;
        this.dateRetourEffective = dateRetourEffective;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }
    public int getIdLivre() {
        return idLivre;
    }
    public int getIdMembre() {
        return idMembre;
    }
    public LocalDate getDateEmprunt() {
        return dateEmprunt;
    }
    public LocalDate getDateRetourPrevue() {
        return dateRetourPrevue;
    }
    public LocalDate getDateRetourEffective() {
        return dateRetourEffective;
    }
    public String getTitreLivre() {
        return titreLivre;
    }
    public void setTitreLivre(String titreLivre) {
        this.titreLivre = titreLivre;
    }
    public String getNomMembre() {
        return nomMembre;
    }
    public void setNomMembre(String nomMembre) {
        this.nomMembre = nomMembre;
    }
}