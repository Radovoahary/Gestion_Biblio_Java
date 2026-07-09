package org.example.demo.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.demo.dao.MembreDAO;
import org.example.demo.model.Membre;

import java.util.Optional;

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

        comboType.setItems(FXCollections.observableArrayList("ETUDIANT", "ENSEIGNANT", "EXTERIEUR"));

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

    // Fonction utilitaire pour afficher les retours utilisateur graphiques
    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void ajouterMembre() {
        // CORRECTION : Validation stricte des entrées et du format email élémentaire
        if (txtNom.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty() || comboType.getValue() == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs vides", "Veuillez remplir l'intégralité du formulaire.");
            return;
        }
        if (!txtEmail.getText().contains("@")) {
            afficherAlerte(Alert.AlertType.WARNING, "Email invalide", "Veuillez entrer une adresse email valide.");
            return;
        }

        Membre m = new Membre(0, txtNom.getText().trim(), txtEmail.getText().trim(), comboType.getValue());
        if (membreDAO.ajouterMembre(m)) {
            rafraichirTableau();
            viderChamps();
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "L'adhérent a été ajouté.");
        } else {
            // CORRECTION : Alerte visuelle en cas de panne DAO ou doublon d'adresse email
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter le membre. L'adresse email est peut-être déjà prise.");
        }
    }

    @FXML
    private void modifierMembre() {
        if (membreSelectionne == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", "Sélectionnez d'abord un membre dans le tableau.");
            return;
        }

        membreSelectionne.setNom(txtNom.getText().trim());
        membreSelectionne.setEmail(txtEmail.getText().trim());
        membreSelectionne.setTypeMembre(comboType.getValue());

        if (membreDAO.modifierMembre(membreSelectionne)) {
            rafraichirTableau();
            viderChamps();
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Fiche membre mise à jour.");
        } else {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "La mise à jour a échoué.");
        }
    }

    @FXML
    private void supprimerMembre() {
        if (membreSelectionne == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner le membre à supprimer.");
            return;
        }

        // CORRECTION : Boîte de dialogue de confirmation avant action irréversible
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer l'adhérent : " + membreSelectionne.getNom() + " ?");
        confirmation.setContentText("Attention, cette action est irréversible.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (membreDAO.supprimerMembre(membreSelectionne.getId())) {
                rafraichirTableau();
                viderChamps();
                afficherAlerte(Alert.AlertType.INFORMATION, "Supprimé", "Le membre a été retiré du système.");
            } else {
                // CORRECTION : Message explicite à l'écran si contrainte de clé étrangère SQL levée
                afficherAlerte(Alert.AlertType.ERROR, "Échec de suppression",
                        "Impossible de supprimer ce membre car il possède un historique d'emprunts actif dans la base.");
            }
        }
    }

    @FXML
    private void viderChamps() {
        txtNom.clear();
        txtEmail.clear();
        comboType.setValue(null);
        tableMembres.getSelectionModel().clearSelection();
        membreSelectionne = null;
    }
}