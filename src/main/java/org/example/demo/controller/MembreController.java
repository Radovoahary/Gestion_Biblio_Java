package org.example.demo.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.demo.dao.MembreDAO;
import org.example.demo.model.Membre;

public class MembreController {
    @FXML private TableView<Membre> tableMembres;
    @FXML private TableColumn<Membre, Integer> colId;
    @FXML private TableColumn<Membre, String> colNom;
    @FXML private TableColumn<Membre, String> colEmail;
    @FXML private TableColumn<Membre, String> colType;

    @FXML private TextField txtNom;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<String> comboType;

    private final MembreDAO membreDAO = new MembreDAO();
    private final ObservableList<Membre> listeMembres = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colType.setCellValueFactory(new PropertyValueFactory<>("typeMembre"));

        comboType.setItems(FXCollections.observableArrayList("ETUDIANT", "ENSEIGNANT"));
        rafraichirTableau();
    }

    private void rafraichirTableau() {
        listeMembres.setAll(membreDAO.getAllMembres());
        tableMembres.setItems(listeMembres);
    }

    @FXML
    private void ajouterMembre() {
        Membre m = new Membre(0, txtNom.getText(), txtEmail.getText(), comboType.getValue());
        if (membreDAO.ajouterMembre(m)) {
            rafraichirTableau();
            txtNom.clear(); txtEmail.clear();
        }
    }
}