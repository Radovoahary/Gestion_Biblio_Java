package org.example.demo.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.demo.dao.LivreDAO;
import org.example.demo.model.Livre;
import java.util.Optional;

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

        tableLivres.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                livreSelectionne = newSel;
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

    private void afficherAlerte(Alert.AlertType type, String titre, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void ajouterLivre() {
        if (txtTitre.getText().trim().isEmpty() || txtAuteur.getText().trim().isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs invalides", "Le titre et l'auteur sont obligatoires.");
            return;
        }
        try {
            Livre l = new Livre(
                    0, txtTitre.getText().trim(), txtAuteur.getText().trim(), txtIsbn.getText().trim(),
                    Integer.parseInt(txtAnnee.getText().trim()),
                    Integer.parseInt(txtDispo.getText().trim()),
                    Integer.parseInt(txtCategorie.getText().trim())
            );
            if (livreDAO.ajouterLivre(l)) {
                rafraichirTableau();
                viderChamps();
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Le livre a été enregistré.");
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "L'insertion en base a échoué.");
            }
        } catch (NumberFormatException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Format incorrect", "L'année, le stock et la catégorie doivent être des nombres.");
        }
    }

    @FXML
    private void modifierLivre() {
        if (livreSelectionne == null) return;
        try {
            livreSelectionne.setTitre(txtTitre.getText().trim());
            livreSelectionne.setAuteur(txtAuteur.getText().trim());
            livreSelectionne.setIsbn(txtIsbn.getText().trim());
            livreSelectionne.setAnnee(Integer.parseInt(txtAnnee.getText().trim()));
            livreSelectionne.setExemplairesDisponibles(Integer.parseInt(txtDispo.getText().trim()));
            livreSelectionne.setCategorieId(Integer.parseInt(txtCategorie.getText().trim()));

            if (livreDAO.modifierLivre(livreSelectionne)) {
                rafraichirTableau();
                viderChamps();
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Livre modifié avec succès.");
            }
        } catch (NumberFormatException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Format incorrect", "Vérifiez vos champs numériques.");
        }
    }

    @FXML
    private void supprimerLivre() {
        if (livreSelectionne == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Sélection requise", "Sélectionnez un livre.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer le livre " + livreSelectionne.getTitre() + " ?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.YES) {
            if (livreDAO.supprimerLivre(livreSelectionne.getId())) {
                rafraichirTableau();
                viderChamps();
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Livre supprimé.");
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer ce livre (des emprunts y sont rattachés).");
            }
        }
    }

    @FXML
    private void filtrerLivres() {
        listeLivres.setAll(livreDAO.rechercherLivres(txtRecherche.getText()));
        tableLivres.setItems(listeLivres);
    }

    @FXML
    private void viderChamps() {
        txtTitre.clear(); txtAuteur.clear(); txtIsbn.clear();
        txtAnnee.clear(); txtDispo.clear(); txtCategorie.clear();
        tableLivres.getSelectionModel().clearSelection();
        livreSelectionne = null;
    }
}