package org.example.demo.controller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.example.demo.dao.EmpruntDAO;
import org.example.demo.dao.LivreDAO;
import org.example.demo.dao.MembreDAO;

import java.io.IOException;

public class MainController {

    @FXML private StackPane contentArea;

    @FXML private Button btnDashboard;
    @FXML private Button btnLivres;
    @FXML private Button btnMembres;
    @FXML private Button btnEmprunts;
    @FXML private Button btnCategories;

    @FXML private Label lblStatLivres;
    @FXML private Label lblStatMembres;
    @FXML private Label lblStatEmprunts;

    private final LivreDAO livreDAO = new LivreDAO();
    private final MembreDAO membreDAO = new MembreDAO();
    private final EmpruntDAO empruntDAO = new EmpruntDAO();

    private Node vueTableauDeBord;

    @FXML
    public void initialize() {
        if (!contentArea.getChildren().isEmpty()) {
            vueTableauDeBord = contentArea.getChildren().get(0);
        }
        rafraichirStatistiques();
        activerBouton(btnDashboard);
    }

    private void chargerSousVue(String fxmlFile, Button boutonActif) {
        try {
            System.out.println("Tentative de chargement de : " + fxmlFile);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo/" + fxmlFile));
            Node nouvelleVue = loader.load();

            nouvelleVue.setOpacity(0.0);
            contentArea.getChildren().setAll(nouvelleVue);

            FadeTransition fondu = new FadeTransition(Duration.millis(400), nouvelleVue);
            fondu.setFromValue(0.0);
            fondu.setToValue(1.0);
            fondu.play();

            activerBouton(boutonActif);

            System.out.println("Vue " + fxmlFile + " injectée avec succès !");
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void activerBouton(Button boutonActif) {
        for (Button b : new Button[]{btnDashboard, btnLivres, btnMembres, btnEmprunts, btnCategories}) {
            if (b != null) {
                b.getStyleClass().remove("menu-button-active");
            }
        }
        if (!boutonActif.getStyleClass().contains("menu-button-active")) {
            boutonActif.getStyleClass().add("menu-button-active");
        }
    }

    @FXML
    private void afficherTableauDeBord() {
        if (vueTableauDeBord != null) {
            contentArea.getChildren().setAll(vueTableauDeBord);
            rafraichirStatistiques();
        }
        activerBouton(btnDashboard);
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

    @FXML
    private void afficherVueCategories() {
        chargerSousVue("categories-view.fxml", btnCategories);
    }

    private void rafraichirStatistiques() {
        if (lblStatLivres != null) {
            lblStatLivres.setText(String.valueOf(livreDAO.getAllLivres().size()));
        }
        if (lblStatMembres != null) {
            lblStatMembres.setText(String.valueOf(membreDAO.getAllMembres().size()));
        }
        if (lblStatEmprunts != null) {
            lblStatEmprunts.setText(String.valueOf(empruntDAO.getAllEmprunts().size()));
        }
    }
}
