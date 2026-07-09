package org.example.demo.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.demo.dao.MembreDAO;
import org.example.demo.model.Membre;

public class MembreController {
    @FXML private TableView<Membre> tableMembres;
    @FXML private TableColumn<Membre, Integer> colId;
    @FXML private TableColumn<Membre, String> colNom;
    @FXML private TableColumn<Membre, String> colEmail;
    @FXML private TableColumn<Membre, String> colType;

    @FXML private TextField txtNom;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<String> comboType;

    private final MembreDAO membreDAO = new MembreDAO();
    private final ObservableList<Membre> listeMembres = FXCollections.observableArrayList();
    private Membre membreSelectionne;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colType.setCellValueFactory(new PropertyValueFactory<>("typeMembre"));

        comboType.setItems(FXCollections.observableArrayList("ETUDIANT", "ENSEIGNANT"));

        // Remplissage automatique des champs lors de la sélection
        tableMembres.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                membreSelectionne = newSelection;
                txtNom.setText(membreSelectionne.getNom());
                txtEmail.setText(membreSelectionne.getEmail());
                comboType.setValue(membreSelectionne.getTypeMembre());
            }
        });

        rafraichirTableau();
    }

    private void rafraichirTableau() {
        listeMembres.setAll(membreDAO.getAllMembres());
        tableMembres.setItems(listeMembres);
    }

    @FXML
    private void ajouterMembre() {
        if (txtNom.getText().isEmpty()) return;
        Membre m = new Membre(0, txtNom.getText(), txtEmail.getText(), comboType.getValue());
        if (membreDAO.ajouterMembre(m)) {
            rafraichirTableau();
            viderChamps();
        }
    }

    @FXML
    private void modifierMembre() {
        if (membreSelectionne == null) return;
        membreSelectionne.setNom(txtNom.getText());
        membreSelectionne.setEmail(txtEmail.getText());
        membreSelectionne.setTypeMembre(comboType.getValue());

        if (membreDAO.modifierMembre(membreSelectionne)) {
            rafraichirTableau();
            viderChamps();
        }
    }

    @FXML
    private void supprimerMembre() {
        if (membreSelectionne == null) return;
        if (membreDAO.supprimerMembre(membreSelectionne.getId())) {
            rafraichirTableau();
            viderChamps();
        }
    }

    @FXML
    private void viderChamps() {
        membreSelectionne = null;
        tableMembres.getSelectionModel().clearSelection();
        txtNom.clear(); txtEmail.clear(); comboType.setValue(null);
    }
}