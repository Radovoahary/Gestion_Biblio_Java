package org.example.demo.dao;

import org.example.demo.config.DatabaseConfig;
import org.example.demo.model.Membre;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MembreDAO {
    // Récupérer tous les membres
    public List<Membre> getAllMembres() {
        List<Membre> liste = new ArrayList<>();
        String query = "SELECT * FROM membres";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                liste.add(new Membre(rs.getInt("id"), rs.getString("nom"), rs.getString("email"), rs.getString("type_membre")));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    // Ajouter un membre
    public boolean ajouterMembre(Membre membre) {
        String query = "INSERT INTO membres (nom, email, type_membre) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, membre.getNom());
            stmt.setString(2, membre.getEmail());
            stmt.setString(3, membre.getTypeMembre());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
}