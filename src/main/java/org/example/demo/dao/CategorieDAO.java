package org.example.demo.dao;

import org.example.demo.config.DatabaseConfig;
import org.example.demo.model.Categorie;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieDAO {

    private String dernierErreur;

    public String getDernierErreur() {
        return dernierErreur;
    }

    // CREATE
    public boolean ajouterCategorie(Categorie categorie) {
        String query = "INSERT INTO categories (nom, description) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, categorie.getNom());
            stmt.setString(2, categorie.getDescription());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            dernierErreur = "Erreur lors de l'ajout de la catégorie (nom déjà utilisé ?) : " + e.getMessage();
            e.printStackTrace();
            return false;
        }
    }

    // READ
    public List<Categorie> getAllCategories() {
        List<Categorie> liste = new ArrayList<>();
        String query = "SELECT * FROM categories ORDER BY nom";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                liste.add(new Categorie(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            dernierErreur = "Erreur lors du chargement des catégories : " + e.getMessage();
            e.printStackTrace();
        }
        return liste;
    }

    // UPDATE
    public boolean modifierCategorie(Categorie categorie) {
        String query = "UPDATE categories SET nom = ?, description = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, categorie.getNom());
            stmt.setString(2, categorie.getDescription());
            stmt.setInt(3, categorie.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            dernierErreur = "Erreur lors de la modification de la catégorie : " + e.getMessage();
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean supprimerCategorie(int id) {
        String query = "DELETE FROM categories WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            dernierErreur = "Erreur lors de la suppression de la catégorie : " + e.getMessage();
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Compte combien de livres utilisent actuellement cette catégorie.
     * Utilisé pour avertir l'utilisateur avant suppression (la contrainte SQL
     * ON DELETE SET NULL n'empêche pas la suppression, elle détache juste les
     * livres concernés - on veut prévenir l'utilisateur avant que ça arrive).
     */
    public int compterLivresAssocies(int categorieId) {
        String query = "SELECT COUNT(*) FROM livres WHERE categorie_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, categorieId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
