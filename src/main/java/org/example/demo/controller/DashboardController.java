package org.example.demo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.demo.dao.LivreDAO;
import org.example.demo.dao.MembreDAO;
import org.example.demo.dao.EmpruntDAO;

public class DashboardController {
    @FXML private Label lblTotalLivres;
    @FXML private Label lblTotalMembres;
    @FXML private Label lblEmpruntsActifs;

    private final LivreDAO livreDAO = new LivreDAO();
    private final MembreDAO membreDAO = new MembreDAO();
    private final EmpruntDAO empruntDAO = new EmpruntDAO();

    @FXML
    public void initialize() {
        // Récupération dynamique depuis la BDD (via les méthodes de comptage à ajouter aux DAO)
        lblTotalLivres.setText(String.valueOf(livreDAO.getAllLivres().size()));
        lblTotalMembres.setText(String.valueOf(membreDAO.getAllMembres().size()));
        lblEmpruntsActifs.setText(String.valueOf(empruntDAO.getAllEmprunts().size()));
    }
}