package org.example.demo.dao;

import org.example.demo.config.DatabaseConfig;
import org.example.demo.model.Livre;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Couche DAO pour la gestion des livres.
 * Regroupe toutes les requêtes SQL complexes liées à la table 'livres'.
 */
public class LivreDAO {

    /**
     * Récupère la liste complète des livres de la bibliothèque.
     */
    public List<Livre> getAllLivres() {
        List<Livre> liste = new ArrayList<>();
        String query = "SELECT * FROM livres";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

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
            System.err.println("❌ Erreur getAllLivres : " + e.getMessage());
        }
        return liste;
    }

    /**
     * Fonctionnalité complète : Ajoute un nouveau livre dans la base de données.
     */
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

            return stmt.executeUpdate() > 0; // Renvoie true si l'insertion a fonctionné
        } catch (SQLException e) {
            System.err.println("❌ Erreur ajouterLivre : " + e.getMessage());
            return false;
        }
    }

    /**
     * Fonctionnalité avancée : Recherche multicritère (titre ou auteur).
     * Idéal pour la barre de recherche dynamique de ton interface JavaFX.
     */
    public List<Livre> rechercherLivres(String motCle) {
        List<Livre> liste = new ArrayList<>();
        String query = "SELECT * FROM livres WHERE titre LIKE ? OR auteur LIKE ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            String p = "%" + motCle + "%";
            stmt.setString(1, p);
            stmt.setString(2, p);

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
            System.err.println("❌ Erreur rechercherLivres : " + e.getMessage());
        }
        return liste;
    }
}