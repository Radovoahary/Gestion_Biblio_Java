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
    private Emprunt empruntSelectionne;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colLivre.setCellValueFactory(new PropertyValueFactory<>("titreLivre"));
        colMembre.setCellValueFactory(new PropertyValueFactory<>("nomMembre"));
        colDateEmprunt.setCellValueFactory(new PropertyValueFactory<>("dateEmprunt"));
        colDatePrevue.setCellValueFactory(new PropertyValueFactory<>("dateRetourPrevue"));

        tableEmprunts.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                empruntSelectionne = newSelection;
                txtIdLivre.setText(String.valueOf(empruntSelectionne.getIdLivre()));
                txtIdMembre.setText(String.valueOf(empruntSelectionne.getIdMembre()));
            }
        });

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
                viderChamps();
            }
        } catch (NumberFormatException e) {
            System.err.println("IDs incorrects.");
        }
    }

    @FXML
    private void retournerLivre() {
        if (empruntSelectionne == null) return;
        if (empruntDAO.retournerLivre(empruntSelectionne.getIdLivre(), empruntSelectionne.getIdMembre())) {
            rafraichirTableau();
            viderChamps();
        }
    }

    @FXML
    private void supprimerEmprunt() {
        if (empruntSelectionne == null) return;
        if (empruntDAO.supprimerEmprunt(empruntSelectionne.getId())) {
            rafraichirTableau();
            viderChamps();
        }
    }

    private void viderChamps() {
        empruntSelectionne = null;
        tableEmprunts.getSelectionModel().clearSelection();
        txtIdLivre.clear(); txtIdMembre.clear();
    }
}