package org.example.demo.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.demo.dao.MembreDAO;
import org.example.demo.model.Membre;
import org.example.demo.util.AlertUtils;

public class MembreController {

    private static final String REGEX_EMAIL = "^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$";

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

    // AMÉLIORATION : validation nom / email / type avant tout envoi en base.
    private boolean champsValides() {
        if (txtNom.getText() == null || txtNom.getText().isBlank()) {
            AlertUtils.erreur("Champ manquant", "Le nom du membre est obligatoire.");
            return false;
        }
        String email = txtEmail.getText();
        if (email != null && !email.isBlank() && !email.matches(REGEX_EMAIL)) {
            AlertUtils.erreur("Email invalide", "Veuillez saisir une adresse email valide.");
            return false;
        }
        if (comboType.getValue() == null) {
            AlertUtils.erreur("Champ manquant", "Veuillez sélectionner un type de membre.");
            return false;
        }
        return true;
    }

    @FXML
    private void ajouterMembre() {
        if (!champsValides()) return;
        Membre m = new Membre(0, txtNom.getText(), txtEmail.getText(), comboType.getValue());
        if (membreDAO.ajouterMembre(m)) {
            rafraichirTableau();
            viderChamps();
        } else {
            AlertUtils.erreur("Échec de l'ajout",
                    membreDAO.getDernierErreur() != null ? membreDAO.getDernierErreur()
                            : "Le membre n'a pas pu être ajouté.");
        }
    }

    @FXML
    private void modifierMembre() {
        if (membreSelectionne == null) {
            AlertUtils.erreur("Aucune sélection", "Veuillez sélectionner un membre avant de le modifier.");
            return;
        }
        if (!champsValides()) return;

        membreSelectionne.setNom(txtNom.getText());
        membreSelectionne.setEmail(txtEmail.getText());
        membreSelectionne.setTypeMembre(comboType.getValue());

        if (membreDAO.modifierMembre(membreSelectionne)) {
            rafraichirTableau();
            viderChamps();
        } else {
            AlertUtils.erreur("Échec de la modification",
                    membreDAO.getDernierErreur() != null ? membreDAO.getDernierErreur()
                            : "Le membre n'a pas pu être modifié.");
        }
    }

    @FXML
    private void supprimerMembre() {
        if (membreSelectionne == null) {
            AlertUtils.erreur("Aucune sélection", "Veuillez sélectionner un membre à supprimer.");
            return;
        }
        boolean confirme = AlertUtils.confirmer("Confirmation",
                "Supprimer définitivement « " + membreSelectionne.getNom() + " » ?");
        if (!confirme) return;

        if (membreDAO.supprimerMembre(membreSelectionne.getId())) {
            rafraichirTableau();
            viderChamps();
        } else {
            AlertUtils.erreur("Suppression impossible",
                    membreDAO.getDernierErreur() != null ? membreDAO.getDernierErreur()
                            : "Ce membre n'a pas pu être supprimé.");
        }
    }

    @FXML
    private void viderChamps() {
        membreSelectionne = null;
        tableMembres.getSelectionModel().clearSelection();
        txtNom.clear(); txtEmail.clear(); comboType.setValue(null);
    }
}
