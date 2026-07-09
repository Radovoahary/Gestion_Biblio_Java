package org.example.demo.dao;

import org.example.demo.config.DatabaseConfig;
import org.example.demo.model.Livre;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivreDAO {

    // CREATE : Ajouter un livre
    public boolean ajouterLivre(Livre livre) {
        String query = "INSERT INTO livres (titre, auteur, isbn, annee, exemplaires_disponibles, categorie_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, livre.getTitre());
            stmt.setString(2, livre.getAuteur());
            stmt.setString(3, livre.getIsbn());
            stmt.setInt(4, livre.getAnnee());
            stmt.setInt(5, livre.getExemplairesDisponibles());
            stmt.setInt(6, livre.getCategorieId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ : Récupérer tous les livres
    public List<Livre> getAllLivres() {
        List<Livre> liste = new ArrayList<>();
        String query = "SELECT * FROM livres";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                liste.add(new Livre(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("auteur"),
                        rs.getString("isbn"),
                        rs.getInt("annee"),
                        rs.getInt("exemplaires_disponibles"),
                        rs.getInt("categorie_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    // READ : Rechercher / Filtrer des livres
    public List<Livre> rechercherLivres(String motCle) {
        List<Livre> liste = new ArrayList<>();
        String query = "SELECT * FROM livres WHERE titre LIKE ? OR auteur LIKE ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + motCle + "%");
            stmt.setString(2, "%" + motCle + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    liste.add(new Livre(
                            rs.getInt("id"),
                            rs.getString("titre"),
                            rs.getString("auteur"),
                            rs.getString("isbn"),
                            rs.getInt("annee"),
                            rs.getInt("exemplaires_disponibles"),
                            rs.getInt("categorie_id")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    // UPDATE : Modifier un livre existant
    public boolean modifierLivre(Livre livre) {
        String query = "UPDATE livres SET titre = ?, auteur = ?, isbn = ?, annee = ?, exemplaires_disponibles = ?, categorie_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, livre.getTitre());
            stmt.setString(2, livre.getAuteur());
            stmt.setString(3, livre.getIsbn());
            stmt.setInt(4, livre.getAnnee());
            stmt.setInt(5, livre.getExemplairesDisponibles());
            stmt.setInt(6, livre.getCategorieId());
            stmt.setInt(7, livre.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE : Supprimer un livre
    public boolean supprimerLivre(int id) {
        String query = "DELETE FROM livres WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Impossible de supprimer : ce livre possède un historique d'emprunts actifs.");
            return false;
        }
    }
}