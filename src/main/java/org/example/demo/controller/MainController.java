package org.example.demo.controller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import java.io.IOException;

public class MainController {
    @FXML private StackPane contentArea;
    @FXML private Button btnDashboard;
    @FXML private Button btnLivres;
    @FXML private Button btnMembres;
    @FXML private Button btnEmprunts;

    @FXML
    public void initialize() {
        // CORRECTION : Charge le dashboard automatiquement dès le lancement de l'application
        afficherDashboard();
    }

    /**
     * Gère le chargement d'une sous-vue et anime la transition
     */
    private void chargerSousVue(String fxmlFile, Button boutonActif) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo/" + fxmlFile));
            Node nouvelleVue = loader.load();

            // --- ANIMATION FADE-IN ---
            nouvelleVue.setOpacity(0.0);
            contentArea.getChildren().setAll(nouvelleVue);
            FadeTransition fondu = new FadeTransition(Duration.millis(300), nouvelleVue);
            fondu.setFromValue(0.0);
            fondu.setToValue(1.0);
            fondu.play();

            // CORRECTION : Met à jour dynamiquement la surbrillance CSS du menu latéral
            mettreAJourMenuStyle(boutonActif);

        } catch (IOException e) {
            System.err.println("Erreur chargement sous-vue: " + e.getMessage());
        }
    }

    private void mettreAJourMenuStyle(Button boutonActif) {
        // Réinitialise tous les boutons
        btnDashboard.getStyleClass().remove("menu-button-active");
        btnLivres.getStyleClass().remove("menu-button-active");
        btnMembres.getStyleClass().remove("menu-button-active");
        btnEmprunts.getStyleClass().remove("menu-button-active");

        // Applique le style uniquement au bouton cliqué
        if (boutonActif != null) {
            boutonActif.getStyleClass().add("menu-button-active");
        }
    }

    @FXML
    private void afficherDashboard() {
        chargerSousVue("dashboard-view.fxml", btnDashboard);
    }

    @FXML
    private void afficherVueLivres() {
        chargerSousVue("livres-view.fxml", btnLivres);
    }

    @FXML
    private void afficherVueMembres() {
        chargerSousVue("membres-view.fxml", btnMembres);
    }

    @FXML
    private void afficherVueEmprunts() {
        chargerSousVue("emprunts-view.fxml", btnEmprunts);
    }
}