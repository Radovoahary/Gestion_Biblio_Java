package org.example.demo.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.demo.dao.LivreDAO;
import org.example.demo.model.Livre;

/**
 * Contrôleur pour la vue de gestion des livres.
 * Relie les éléments graphiques (FXML) aux requêtes de la BDD (LivreDAO).
 */
public class LivreController {

    // Éléments de la table
    @FXML private TableView<Livre> tableLivres;
    @FXML private TableColumn<Livre, Integer> colId;
    @FXML private TableColumn<Livre, String> colTitre;
    @FXML private TableColumn<Livre, String> colAuteur;
    @FXML private TableColumn<Livre, String> colIsbn;
    @FXML private TableColumn<Livre, Integer> colAnnee;
    @FXML private TableColumn<Livre, Integer> colDispo;

    // Éléments des formulaires
    @FXML private TextField txtRecherche;
    @FXML private TextField txtTitre;
    @FXML private TextField txtAuteur;
    @FXML private TextField txtIsbn;
    @FXML private TextField txtAnnee;
    @FXML private TextField txtDispo;
    @FXML private TextField txtCategorie;

    private final LivreDAO livreDAO = new LivreDAO();
    private final ObservableList<Livre> listeLivres = FXCollections.observableArrayList();

    /**
     * Méthode exécutée automatiquement par JavaFX à l'affichage de la vue.
     * Configure les colonnes du tableau.
     */
    @FXML
    public void initialize() {
        // Liaison des colonnes avec les attributs de la classe Livre (via leurs Getters)
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colAuteur.setCellValueFactory(new PropertyValueFactory<>("auteur"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colAnnee.setCellValueFactory(new PropertyValueFactory<>("annee"));
        colDispo.setCellValueFactory(new PropertyValueFactory<>("exemplairesDisponibles"));

        // Chargement initial des données depuis MySQL
        rafraichirTableau();
    }


    //  Recharge les livres depuis la base de données et met à jour l'affichage.

    private void rafraichirTableau() {
        listeLivres.clear();
        listeLivres.addAll(livreDAO.getAllLivres());
        tableLivres.setItems(listeLivres);
    }


    //  Récupère les saisies et ajoute le livre en BDD.

    @FXML
    private void ajouterLivre() {
        try {
            Livre nouveau = new Livre(
                    0, // L'ID sera généré automatiquement par la BDD (Auto-increment)
                    txtTitre.getText(),
                    txtAuteur.getText(),
                    txtIsbn.getText(),
                    Integer.parseInt(txtAnnee.getText()),
                    Integer.parseInt(txtDispo.getText()),
                    Integer.parseInt(txtCategorie.getText())
            );

            if (livreDAO.ajouterLivre(nouveau)) {
                rafraichirTableau();
                viderChamps();
            }
        } catch (NumberFormatException e) {
            System.err.println(" Erreur : Vérifie le format des nombres (Année, Exemplaires, Catégorie).");
        }
    }

    /**
     * Filtre dynamiquement le tableau selon la saisie de recherche.
     */
    @FXML
    private void filtrerLivres() {
        String motCle = txtRecherche.getText();
        listeLivres.clear();
        listeLivres.addAll(livreDAO.rechercherLivres(motCle));
        tableLivres.setItems(listeLivres);
    }

    private void viderChamps() {
        txtTitre.clear();
        txtAuteur.clear();
        txtIsbn.clear();
        txtAnnee.clear();
        txtDispo.clear();
        txtCategorie.clear();
    }
}