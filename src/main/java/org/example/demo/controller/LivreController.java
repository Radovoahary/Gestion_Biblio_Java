package org.example.demo.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.demo.dao.LivreDAO;
import org.example.demo.model.Livre;
import org.example.demo.util.AlertUtils;

public class LivreController {

    @FXML private TableView<Livre> tableLivres;
    @FXML private TableColumn<Livre, Integer> colId;
    @FXML private TableColumn<Livre, String> colTitre;
    @FXML private TableColumn<Livre, String> colAuteur;
    @FXML private TableColumn<Livre, String> colIsbn;
    @FXML private TableColumn<Livre, Integer> colAnnee;
    @FXML private TableColumn<Livre, Integer> colDispo;

    @FXML private TextField txtRecherche;
    @FXML private TextField txtTitre;
    @FXML private TextField txtAuteur;
    @FXML private TextField txtIsbn;
    @FXML private TextField txtAnnee;
    @FXML private TextField txtDispo;
    @FXML private TextField txtCategorie;

    private final LivreDAO livreDAO = new LivreDAO();
    private final ObservableList<Livre> listeLivres = FXCollections.observableArrayList();
    private Livre livreSelectionne;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colAuteur.setCellValueFactory(new PropertyValueFactory<>("auteur"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colAnnee.setCellValueFactory(new PropertyValueFactory<>("annee"));
        colDispo.setCellValueFactory(new PropertyValueFactory<>("exemplairesDisponibles"));

        tableLivres.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                livreSelectionne = newSelection;
                txtTitre.setText(livreSelectionne.getTitre());
                txtAuteur.setText(livreSelectionne.getAuteur());
                txtIsbn.setText(livreSelectionne.getIsbn());
                txtAnnee.setText(String.valueOf(livreSelectionne.getAnnee()));
                txtDispo.setText(String.valueOf(livreSelectionne.getExemplairesDisponibles()));
                txtCategorie.setText(String.valueOf(livreSelectionne.getCategorieId()));
            }
        });

        rafraichirTableau();
    }

    private void rafraichirTableau() {
        listeLivres.setAll(livreDAO.getAllLivres());
        tableLivres.setItems(listeLivres);
    }

    // AMÉLIORATION : validation des champs obligatoires avant tout envoi en base,
    // avec message clair au lieu d'un échec silencieux.
    private boolean champsValides() {
        if (txtTitre.getText() == null || txtTitre.getText().isBlank()) {
            AlertUtils.erreur("Champ manquant", "Le titre du livre est obligatoire.");
            return false;
        }
        if (txtAuteur.getText() == null || txtAuteur.getText().isBlank()) {
            AlertUtils.erreur("Champ manquant", "L'auteur du livre est obligatoire.");
            return false;
        }
        return true;
    }

    @FXML
    private void ajouterLivre() {
        if (!champsValides()) return;
        try {
            Livre nouveau = new Livre(0, txtTitre.getText(), txtAuteur.getText(), txtIsbn.getText(),
                    Integer.parseInt(txtAnnee.getText()), Integer.parseInt(txtDispo.getText()),
                    Integer.parseInt(txtCategorie.getText()));

            if (livreDAO.ajouterLivre(nouveau)) {
                rafraichirTableau();
                viderChamps();
            } else {
                AlertUtils.erreur("Échec de l'ajout",
                        livreDAO.getDernierErreur() != null ? livreDAO.getDernierErreur()
                                : "Le livre n'a pas pu être ajouté.");
            }
        } catch (NumberFormatException e) {
            AlertUtils.erreur("Format invalide",
                    "Année, exemplaires disponibles et catégorie doivent être des nombres entiers.");
        }
    }

    @FXML
    private void modifierLivre() {
        if (livreSelectionne == null) {
            AlertUtils.erreur("Aucune sélection", "Veuillez sélectionner un livre dans le tableau avant de le modifier.");
            return;
        }
        if (!champsValides()) return;
        try {
            livreSelectionne.setTitre(txtTitre.getText());
            livreSelectionne.setAuteur(txtAuteur.getText());
            livreSelectionne.setIsbn(txtIsbn.getText());
            livreSelectionne.setAnnee(Integer.parseInt(txtAnnee.getText()));
            livreSelectionne.setExemplairesDisponibles(Integer.parseInt(txtDispo.getText()));
            livreSelectionne.setCategorieId(Integer.parseInt(txtCategorie.getText()));

            if (livreDAO.modifierLivre(livreSelectionne)) {
                rafraichirTableau();
                viderChamps();
            } else {
                AlertUtils.erreur("Échec de la modification",
                        livreDAO.getDernierErreur() != null ? livreDAO.getDernierErreur()
                                : "Le livre n'a pas pu être modifié.");
            }
        } catch (NumberFormatException e) {
            AlertUtils.erreur("Format invalide",
                    "Année, exemplaires disponibles et catégorie doivent être des nombres entiers.");
        }
    }

    @FXML
    private void supprimerLivre() {
        if (livreSelectionne == null) {
            AlertUtils.erreur("Aucune sélection", "Veuillez sélectionner un livre à supprimer.");
            return;
        }
        // AMÉLIORATION : confirmation avant toute suppression définitive.
        boolean confirme = AlertUtils.confirmer("Confirmation",
                "Supprimer définitivement « " + livreSelectionne.getTitre() + " » ?");
        if (!confirme) return;

        if (livreDAO.supprimerLivre(livreSelectionne.getId())) {
            rafraichirTableau();
            viderChamps();
        } else {
            AlertUtils.erreur("Suppression impossible",
                    livreDAO.getDernierErreur() != null ? livreDAO.getDernierErreur()
                            : "Ce livre n'a pas pu être supprimé.");
        }
    }

    @FXML
    private void filtrerLivres() {
        String motCle = txtRecherche.getText();
        if (motCle == null || motCle.isBlank()) {
            rafraichirTableau();
            return;
        }
        listeLivres.setAll(livreDAO.rechercherLivres(motCle));
        tableLivres.setItems(listeLivres);
    }

    @FXML
    private void viderChamps() {
        livreSelectionne = null;
        tableLivres.getSelectionModel().clearSelection();
        txtTitre.clear(); txtAuteur.clear(); txtIsbn.clear();
        txtAnnee.clear(); txtDispo.clear(); txtCategorie.clear();
    }
}
