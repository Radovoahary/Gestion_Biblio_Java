package org.example.demo.dao;

import org.example.demo.config.DatabaseConfig;
import org.example.demo.model.Membre;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MembreDAO {

    private String dernierErreur;

    public String getDernierErreur() {
        return dernierErreur;
    }

    // CREATE : Ajouter un membre
    public boolean ajouterMembre(Membre membre) {
        String query = "INSERT INTO membres (nom, email, type_membre) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, membre.getNom());
            stmt.setString(2, membre.getEmail());
            stmt.setString(3, membre.getTypeMembre());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            dernierErreur = "Erreur lors de l'ajout du membre (email en double ?) : " + e.getMessage();
            e.printStackTrace();
            return false;
        }
    }

    // READ : Récupérer tous les membres
    public List<Membre> getAllMembres() {
        List<Membre> liste = new ArrayList<>();
        String query = "SELECT * FROM membres";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                liste.add(new Membre(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("email"),
                        rs.getString("type_membre")
                ));
            }
        } catch (SQLException e) {
            dernierErreur = "Erreur lors du chargement des membres : " + e.getMessage();
            e.printStackTrace();
        }
        return liste;
    }

    // UPDATE : Modifier un membre
    public boolean modifierMembre(Membre membre) {
        String query = "UPDATE membres SET nom = ?, email = ?, type_membre = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, membre.getNom());
            stmt.setString(2, membre.getEmail());
            stmt.setString(3, membre.getTypeMembre());
            stmt.setInt(4, membre.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            dernierErreur = "Erreur lors de la modification du membre : " + e.getMessage();
            e.printStackTrace();
            return false;
        }
    }

    // DELETE : Supprimer un membre
    public boolean supprimerMembre(int id) {
        String query = "DELETE FROM membres WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            dernierErreur = "Impossible de supprimer : ce membre possède des fiches d'emprunts associées.";
            System.err.println("❌ " + dernierErreur);
            return false;
        }
    }
}
