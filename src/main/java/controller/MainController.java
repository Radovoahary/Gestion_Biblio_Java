package org.example.demo.controller;


import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

/**
 * Contrôleur principal de l'application.
 * Gère la navigation globale de la barre latérale.
 */
public class MainController {

    @FXML
    private StackPane contentArea; // Zone centrale dynamique

    @FXML
    private void afficherVueLivres() {
        System.out.println("Clic sur Gestion des Livres");
        // Logique de chargement de la sous-vue à venir
    }

    @FXML
    private void afficherVueMembres() {
        System.out.println("Clic sur Gestion des Membres");
    }

    @FXML
    private void afficherVueEmprunts() {
        System.out.println("Clic sur Emprunts");
    }
}