package org.example.demo.model;

import java.time.LocalDate;

public class Emprunt {

    private int id;
    private int idLivre;
    private int idMembre;
    private LocalDate dateEmprunt;
    private LocalDate dateRetourPrevue;
    private LocalDate dateRetourEffective; // null si l'emprunt est toujours en cours

    // Champs "bonus" injectés par EmpruntDAO pour l'affichage (jointure SQL)
    private String titreLivre;
    private String nomMembre;

    public Emprunt() {
    }

    public Emprunt(int id, int idLivre, int idMembre, LocalDate dateEmprunt,
                    LocalDate dateRetourPrevue, LocalDate dateRetourEffective) {
        this.id = id;
        this.idLivre = idLivre;
        this.idMembre = idMembre;
        this.dateEmprunt = dateEmprunt;
        this.dateRetourPrevue = dateRetourPrevue;
        this.dateRetourEffective = dateRetourEffective;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdLivre() {
        return idLivre;
    }

    public void setIdLivre(int idLivre) {
        this.idLivre = idLivre;
    }

    public int getIdMembre() {
        return idMembre;
    }

    public void setIdMembre(int idMembre) {
        this.idMembre = idMembre;
    }

    public LocalDate getDateEmprunt() {
        return dateEmprunt;
    }

    public void setDateEmprunt(LocalDate dateEmprunt) {
        this.dateEmprunt = dateEmprunt;
    }

    public LocalDate getDateRetourPrevue() {
        return dateRetourPrevue;
    }

    public void setDateRetourPrevue(LocalDate dateRetourPrevue) {
        this.dateRetourPrevue = dateRetourPrevue;
    }

    public LocalDate getDateRetourEffective() {
        return dateRetourEffective;
    }

    public void setDateRetourEffective(LocalDate dateRetourEffective) {
        this.dateRetourEffective = dateRetourEffective;
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

    /**
     * AMÉLIORATION : permet de savoir si l'emprunt est en retard,
     * utilisable pour colorer la ligne en rouge dans le TableView
     * (voir suggestion de CellFactory dans EmpruntController).
     */
    public boolean isEnRetard() {
        return dateRetourEffective == null
                && dateRetourPrevue != null
                && LocalDate.now().isAfter(dateRetourPrevue);
    }

    @Override
    public String toString() {
        return "Emprunt{id=" + id + ", idLivre=" + idLivre + ", idMembre=" + idMembre +
                ", dateEmprunt=" + dateEmprunt + ", dateRetourPrevue=" + dateRetourPrevue +
                ", dateRetourEffective=" + dateRetourEffective + '}';
    }
}
