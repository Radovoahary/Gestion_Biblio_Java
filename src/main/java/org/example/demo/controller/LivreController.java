package org.example.demo.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.demo.dao.LivreDAO;
import org.example.demo.model.Livre;

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

        // Écouteur pour charger la ligne sélectionnée dans le formulaire de modification
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
        listeLivres.clear();
        listeLivres.addAll(livreDAO.getAllLivres());
        tableLivres.setItems(listeLivres);
    }

    @FXML
    private void ajouterLivre() {
        try {
            Livre nouveau = new Livre(0, txtTitre.getText(), txtAuteur.getText(), txtIsbn.getText(),
                    Integer.parseInt(txtAnnee.getText()), Integer.parseInt(txtDispo.getText()), Integer.parseInt(txtCategorie.getText()));

            if (livreDAO.ajouterLivre(nouveau)) {
                rafraichirTableau();
                viderChamps();
            }
        } catch (NumberFormatException e) {
            System.err.println("Champs numériques mal formatés.");
        }
    }

    @FXML
    private void modifierLivre() {
        if (livreSelectionne == null) return;
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
            }
        } catch (NumberFormatException e) {
            System.err.println("Champs numériques mal formatés.");
        }
    }

    @FXML
    private void supprimerLivre() {
        if (livreSelectionne == null) return;
        if (livreDAO.supprimerLivre(livreSelectionne.getId())) {
            rafraichirTableau();
            viderChamps();
        }
    }

    @FXML
    private void filtrerLivres() {
        String motCle = txtRecherche.getText();
        listeLivres.clear();
        listeLivres.addAll(livreDAO.rechercherLivres(motCle));
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