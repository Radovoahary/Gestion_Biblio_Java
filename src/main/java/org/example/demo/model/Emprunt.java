package org.example.demo.model;

import java.time.LocalDate;

/**
 * Représente l'emprunt d'un livre par un membre.
 * Fait le lien entre la table 'membres' et 'livres'.
 */
public class Emprunt {
    private int id;
    private int membreId;
    private int livreId;
    private LocalDate dateEmprunt;
    private LocalDate dateRetourPrevu;
    private LocalDate dateRetourReel; // Peut être null si le livre n'est pas encore rendu

    // Constructeur complet
    public Emprunt(int id, int membreId, int livreId, LocalDate dateEmprunt, LocalDate dateRetourPrevu, LocalDate dateRetourReel) {
        this.id = id;
        this.membreId = membreId;
        this.livreId = livreId;
        this.dateEmprunt = dateEmprunt;
        this.dateRetourPrevu = dateRetourPrevu;
        this.dateRetourReel = dateRetourReel;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getMembreId() {
        return membreId;
    }
    public void setMembreId(int membreId) {
        this.membreId = membreId;
    }

    public int getLivreId() {
        return livreId;
    }
    public void setLivreId(int livreId) {
        this.livreId = livreId;
    }

    public LocalDate getDateEmprunt() {
        return dateEmprunt;
    }
    public void setDateEmprunt(LocalDate dateEmprunt) {
        this.dateEmprunt = dateEmprunt;
    }

    public LocalDate getDateRetourPrevu() {
        return dateRetourPrevu;
    }
    public void setDateRetourPrevu(LocalDate dateRetourPrevu) {
        this.dateRetourPrevu = dateRetourPrevu;
    }

    public LocalDate getDateRetourReel() {
        return dateRetourReel;
    }
    public void setDateRetourReel(LocalDate dateRetourReel) {
        this.dateRetourReel = dateRetourReel;
    }
}