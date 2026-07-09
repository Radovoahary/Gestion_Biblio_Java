package org.example.demo.controller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import java.io.IOException;

public class MainController {
    @FXML
    private StackPane contentArea;

    /**
     * Charge une sous-vue (.fxml) de manière dynamique au centre de l'application
     * avec une animation de fondu fluide (Fade In).
     */
    private void chargerSousVue(String fxmlFile) {
        try {
            System.out.println(" Tentative de chargement de : " + fxmlFile);

            // Chargement du fichier FXML depuis le dossier de ressources
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo/" + fxmlFile));
            Node nouvelleVue = loader.load();

            // --- ANIMATION ---
            nouvelleVue.setOpacity(0.0);

            // On injecte la nouvelle vue dans le conteneur central du borderPane
            contentArea.getChildren().setAll(nouvelleVue);

            // Transition de fondu de 400 millisecondes
            FadeTransition fondu = new FadeTransition(Duration.millis(400), nouvelleVue);
            fondu.setFromValue(0.0); // De invisible...
            fondu.setToValue(1.0);   // ... à 100% visible
            fondu.play();            // Lancement de l'animation
            // -------------------------------

            System.out.println(" Vue " + fxmlFile + " injectée avec succès !");
        } catch (IOException e) {
            System.err.println(" Erreur lors du chargement de la vue : " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Actions des boutons du menu latéral

    @FXML
    private void afficherVueLivres() {
        System.out.println(" Clic détecté sur : Livres");
        chargerSousVue("livres-view.fxml");
    }

    @FXML
    private void afficherVueMembres() {
        System.out.println(" Clic détecté sur : Membres");
        chargerSousVue("membres-view.fxml");
    }

    @FXML
    private void afficherVueEmprunts() {
        System.out.println(" Clic détecté sur : Emprunts");
        chargerSousVue("emprunts-view.fxml");
    }
}