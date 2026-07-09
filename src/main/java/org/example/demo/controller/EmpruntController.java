package org.example.demo.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.demo.dao.EmpruntDAO;
import org.example.demo.model.Emprunt;
import java.time.LocalDate;

public class EmpruntController {
    @FXML private TableView<Emprunt> tableEmprunts;
    @FXML private TableColumn<Emprunt, Integer> colId;
    @FXML private TableColumn<Emprunt, String> colLivre;
    @FXML private TableColumn<Emprunt, String> colMembre;
    @FXML private TableColumn<Emprunt, LocalDate> colDateEmprunt;
    @FXML private TableColumn<Emprunt, LocalDate> colDatePrevue;

    @FXML private TextField txtIdLivre;
    @FXML private TextField txtIdMembre;

    private final EmpruntDAO empruntDAO = new EmpruntDAO();
    private final ObservableList<Emprunt> listeEmprunts = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colLivre.setCellValueFactory(new PropertyValueFactory<>("titreLivre"));
        colMembre.setCellValueFactory(new PropertyValueFactory<>("nomMembre"));
        colDateEmprunt.setCellValueFactory(new PropertyValueFactory<>("dateEmprunt"));
        colDatePrevue.setCellValueFactory(new PropertyValueFactory<>("dateRetourPrevue"));

        rafraichirTableau();
    }

    private void rafraichirTableau() {
        listeEmprunts.setAll(empruntDAO.getAllEmprunts());
        tableEmprunts.setItems(listeEmprunts);
    }

    @FXML
    private void validerEmprunt() {
        try {
            int idLivre = Integer.parseInt(txtIdLivre.getText());
            int idMembre = Integer.parseInt(txtIdMembre.getText());

            if (empruntDAO.enregistrerEmprunt(idLivre, idMembre)) {
                rafraichirTableau();
                txtIdLivre.clear();
                txtIdMembre.clear();
            }
        } catch (NumberFormatException e) {
            System.err.println("⚠️ ID invalides.");
        }
    }
}