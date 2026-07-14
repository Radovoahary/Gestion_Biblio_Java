package org.example.demo.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.demo.dao.CategorieDAO;
import org.example.demo.model.Categorie;
import org.example.demo.util.AlertUtils;

public class CategorieController {

    @FXML private TableView<Categorie> tableCategories;
    @FXML private TableColumn<Categorie, Integer> colId;
    @FXML private TableColumn<Categorie, String> colNom;
    @FXML private TableColumn<Categorie, String> colDescription;

    @FXML private TextField txtNom;
    @FXML private TextField txtDescription;

    private final CategorieDAO categorieDAO = new CategorieDAO();
    private final ObservableList<Categorie> listeCategories = FXCollections.observableArrayList();
    private Categorie categorieSelectionnee;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        tableCategories.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                categorieSelectionnee = newSelection;
                txtNom.setText(categorieSelectionnee.getNom());
                txtDescription.setText(categorieSelectionnee.getDescription());
            }
        });

        rafraichirTableau();
    }

    private void rafraichirTableau() {
        listeCategories.setAll(categorieDAO.getAllCategories());
        tableCategories.setItems(listeCategories);
    }

    private boolean champsValides() {
        if (txtNom.getText() == null || txtNom.getText().isBlank()) {
            AlertUtils.erreur("Champ manquant", "Le nom de la catégorie est obligatoire.");
            return false;
        }
        return true;
    }

    @FXML
    private void ajouterCategorie() {
        if (!champsValides()) return;
        Categorie c = new Categorie(0, txtNom.getText(), txtDescription.getText());
        if (categorieDAO.ajouterCategorie(c)) {
            rafraichirTableau();
            viderChamps();
        } else {
            AlertUtils.erreur("Échec de l'ajout",
                    categorieDAO.getDernierErreur() != null ? categorieDAO.getDernierErreur()
                            : "La catégorie n'a pas pu être ajoutée.");
        }
    }

    @FXML
    private void modifierCategorie() {
        if (categorieSelectionnee == null) {
            AlertUtils.erreur("Aucune sélection", "Veuillez sélectionner une catégorie à modifier.");
            return;
        }
        if (!champsValides()) return;

        categorieSelectionnee.setNom(txtNom.getText());
        categorieSelectionnee.setDescription(txtDescription.getText());

        if (categorieDAO.modifierCategorie(categorieSelectionnee)) {
            rafraichirTableau();
            viderChamps();
        } else {
            AlertUtils.erreur("Échec de la modification",
                    categorieDAO.getDernierErreur() != null ? categorieDAO.getDernierErreur()
                            : "La catégorie n'a pas pu être modifiée.");
        }
    }

    @FXML
    private void supprimerCategorie() {
        if (categorieSelectionnee == null) {
            AlertUtils.erreur("Aucune sélection", "Veuillez sélectionner une catégorie à supprimer.");
            return;
        }

        // AMÉLIORATION : on prévient l'utilisateur si des livres dépendent
        // encore de cette catégorie avant de la supprimer définitivement.
        int nbLivres = categorieDAO.compterLivresAssocies(categorieSelectionnee.getId());
        String message = "Supprimer définitivement « " + categorieSelectionnee.getNom() + " » ?";
        if (nbLivres > 0) {
            message += "\n\n⚠️ " + nbLivres + " livre(s) sont actuellement rattachés à cette catégorie. "
                    + "Ils perdront leur catégorie (mise à vide).";
        }
        boolean confirme = AlertUtils.confirmer("Confirmation", message);
        if (!confirme) return;

        if (categorieDAO.supprimerCategorie(categorieSelectionnee.getId())) {
            rafraichirTableau();
            viderChamps();
        } else {
            AlertUtils.erreur("Suppression impossible",
                    categorieDAO.getDernierErreur() != null ? categorieDAO.getDernierErreur()
                            : "Cette catégorie n'a pas pu être supprimée.");
        }
    }

    @FXML
    private void viderChamps() {
        categorieSelectionnee = null;
        tableCategories.getSelectionModel().clearSelection();
        txtNom.clear();
        txtDescription.clear();
    }
}
