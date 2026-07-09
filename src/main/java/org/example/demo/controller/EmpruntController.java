package org.example.demo.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.demo.dao.EmpruntDAO;
import org.example.demo.model.Emprunt;
import java.time.LocalDate;
import java.util.Optional;

public class EmpruntController {
    @FXML private TableView<Emprunt> tableEmprunts;
    @FXML private TableColumn<Emprunt, Integer> colId;
    @FXML private TableColumn<Emprunt, String> colLivre;
    @FXML private TableColumn<Emprunt, String> colMembre;
    @FXML private TableColumn<Emprunt, LocalDate> colDateEmprunt;
    @FXML private TableColumn<Emprunt, LocalDate> colDatePrevue;

    @FXML private TextField txtIdLivre;
    @FXML private TextField txtIdMembre;

    private final EmpruntDAO empruntDAO = new EmpruntDAO();
    private final ObservableList<Emprunt> listeEmprunts = FXCollections.observableArrayList();
    private Emprunt empruntSelectionne;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colLivre.setCellValueFactory(new PropertyValueFactory<>("titreLivre"));
        colMembre.setCellValueFactory(new PropertyValueFactory<>("nomMembre"));
        colDateEmprunt.setCellValueFactory(new PropertyValueFactory<>("dateEmprunt"));
        colDatePrevue.setCellValueFactory(new PropertyValueFactory<>("dateRetourPrevue"));

        tableEmprunts.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                empruntSelectionne = newSel;
                txtIdLivre.setText(String.valueOf(empruntSelectionne.getIdLivre()));
                txtIdMembre.setText(String.valueOf(empruntSelectionne.getIdMembre()));
            }
        });
        rafraichirTableau();
    }

    private void rafraichirTableau() {
        listeEmprunts.setAll(empruntDAO.getAllEmprunts());
        tableEmprunts.setItems(listeEmprunts);
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void validerEmprunt() {
        try {
            int idLivre = Integer.parseInt(txtIdLivre.getText().trim());
            int idMembre = Integer.parseInt(txtIdMembre.getText().trim());

            if (empruntDAO.enregistrerEmprunt(idLivre, idMembre)) {
                rafraichirTableau();
                viderChamps();
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "L'emprunt a bien été enregistré.");
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible de valider l'emprunt (vérifiez les ID et le stock de livres).");
            }
        } catch (NumberFormatException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Format incorrect", "Les ID du livre et du membre doivent être des nombres.");
        }
    }

    @FXML
    private void retournerLivre() {
        if (empruntSelectionne == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Sélection requise", "Veuillez sélectionner une ligne d'emprunt active.");
            return;
        }
        if (empruntDAO.retournerLivre(empruntSelectionne.getIdLivre(), empruntSelectionne.getIdMembre())) {
            rafraichirTableau();
            viderChamps();
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Le livre a été enregistré comme retourné.");
        } else {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Le traitement du retour a échoué.");
        }
    }

    @FXML
    private void supprimerEmprunt() {
        if (empruntSelectionne == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer définitivement cet enregistrement d'emprunt ?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.YES) {
            if (empruntDAO.supprimerEmprunt(empruntSelectionne.getId())) {
                rafraichirTableau();
                viderChamps();
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Fiche d'emprunt supprimée.");
            }
        }
    }

    private void viderChamps() {
        txtIdLivre.clear();
        txtIdMembre.clear();
        tableEmprunts.getSelectionModel().clearSelection();
        empruntSelectionne = null;
    }
}