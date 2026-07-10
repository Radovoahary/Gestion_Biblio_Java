package org.example.demo.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.demo.dao.EmpruntDAO;
import org.example.demo.model.Emprunt;
import org.example.demo.util.AlertUtils;

import java.time.LocalDate;

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

        // AMÉLIORATION : les lignes en retard (date prévue dépassée, non retournées)
        // sont surlignées en rouge pâle pour attirer l'attention du bibliothécaire.
        tableEmprunts.setRowFactory(tv -> new TableRow<Emprunt>() {
            @Override
            protected void updateItem(Emprunt emprunt, boolean empty) {
                super.updateItem(emprunt, empty);
                if (empty || emprunt == null) {
                    setStyle("");
                } else if (emprunt.isEnRetard()) {
                    setStyle("-fx-background-color: #4a1a1a;");
                } else {
                    setStyle("");
                }
            }
        });

        tableEmprunts.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                empruntSelectionne = newSelection;
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

    @FXML
    private void validerEmprunt() {
        try {
            int idLivre = Integer.parseInt(txtIdLivre.getText());
            int idMembre = Integer.parseInt(txtIdMembre.getText());

            if (empruntDAO.enregistrerEmprunt(idLivre, idMembre)) {
                rafraichirTableau();
                viderChamps();
            } else {
                AlertUtils.erreur("Emprunt refusé",
                        empruntDAO.getDernierErreur() != null ? empruntDAO.getDernierErreur()
                                : "L'emprunt n'a pas pu être enregistré.");
            }
        } catch (NumberFormatException e) {
            AlertUtils.erreur("Identifiants invalides",
                    "L'ID du livre et l'ID du membre doivent être des nombres entiers.");
        }
    }

    @FXML
    private void retournerLivre() {
        if (empruntSelectionne == null) {
            AlertUtils.erreur("Aucune sélection", "Veuillez sélectionner un emprunt à clôturer.");
            return;
        }
        if (empruntDAO.retournerLivre(empruntSelectionne.getIdLivre(), empruntSelectionne.getIdMembre())) {
            rafraichirTableau();
            viderChamps();
        } else {
            AlertUtils.erreur("Échec",
                    empruntDAO.getDernierErreur() != null ? empruntDAO.getDernierErreur()
                            : "Le retour n'a pas pu être enregistré.");
        }
    }

    @FXML
    private void supprimerEmprunt() {
        if (empruntSelectionne == null) {
            AlertUtils.erreur("Aucune sélection", "Veuillez sélectionner une fiche à supprimer.");
            return;
        }
        boolean confirme = AlertUtils.confirmer("Confirmation", "Supprimer définitivement cette fiche d'emprunt ?");
        if (!confirme) return;

        if (empruntDAO.supprimerEmprunt(empruntSelectionne.getId())) {
            rafraichirTableau();
            viderChamps();
        } else {
            AlertUtils.erreur("Échec",
                    empruntDAO.getDernierErreur() != null ? empruntDAO.getDernierErreur()
                            : "La fiche d'emprunt n'a pas pu être supprimée.");
        }
    }

    @FXML
    private void viderChamps() {
        empruntSelectionne = null;
        tableEmprunts.getSelectionModel().clearSelection();
        txtIdLivre.clear(); txtIdMembre.clear();
    }
}
