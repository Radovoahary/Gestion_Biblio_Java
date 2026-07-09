package org.example.demo.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentArea; // Zone centrale définie dans main-view.fxml

    private void chargerSousVue(String fxmlFileName) {
        try {
            // Chargement du fichier FXML depuis le dossier des ressources
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo/" + fxmlFileName));
            Parent vue = loader.load();

            // Nettoyage de la zone centrale et injection de la nouvelle vue
            contentArea.getChildren().clear();
            contentArea.getChildren().add(vue);

        } catch (IOException e) {
            System.err.println("Impossible de charger la vue : " + fxmlFileName);
            e.printStackTrace();
        }
    }

    @FXML
    private void afficherVueLivres() {
        System.out.println(" Chargement de la vue des Livres...");
        chargerSousVue("livres-view.fxml");
    }

    @FXML
    private void afficherVueMembres() {
        System.out.println(" Chargement de la vue des Membres...");
    }

    @FXML
    private void afficherVueEmprunts() {
        System.out.println(" Chargement de la vue des Emprunts...");
    }
}