package org.example.demo.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.net.URL;

/**
 * Contrôleur principal de l'application.
 * Gère le changement dynamique des vues au centre de l'écran.
 */
public class MainController {

    @FXML
    private StackPane contentArea; // Zone centrale définie dans main-view.fxml


     // Charge de manière dynamique un fichier FXML au centre de la fenêtre.

    private void chargerSousVue(String fxmlFileName) {
        String cheminComplet = "/org/example/demo/" + fxmlFileName;
        System.out.println(" Tentative de chargement de : " + cheminComplet);

        URL fxmlUrl = getClass().getResource(cheminComplet);

        if (fxmlUrl == null) {
            System.err.println(" Fatal error : Le fichier FXML est introuvable au chemin : " + cheminComplet);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent vue = loader.load();

            // Nettoyage de la zone centrale et injection de la nouvelle vue
            contentArea.getChildren().clear();
            contentArea.getChildren().add(vue);
            System.out.println(" Vue " + fxmlFileName + " injectée avec succès !");

        } catch (IOException e) {
            System.err.println(" Erreur de parsing du fichier FXML : " + fxmlFileName);
            e.printStackTrace();
        }
    }

    @FXML
    private void afficherVueLivres() {
        System.out.println(" Clic détecté sur : Gestion des Livres");
        chargerSousVue("livres-view.fxml");
    }

    @FXML
    private void afficherVueMembres() {
        System.out.println(" Clic détecté sur : Gestion des Membres");
        chargerSousVue("membres-view.fxml");
    }

    @FXML
    private void afficherVueEmprunts() {
        System.out.println(" Clic détecté sur : Emprunts");
        // La vue emprunts sera gérée à l'étape suivante
    }
}