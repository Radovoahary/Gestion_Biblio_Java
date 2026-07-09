package org.example.demo.dao;

import org.example.demo.config.DatabaseConfig;
import org.example.demo.model.Emprunt;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmpruntDAO {

    /**
     * Récupère tous les emprunts avec les titres des livres et noms des membres associés (Via JOIN)
     */
    public List<Emprunt> getAllEmprunts() {
        List<Emprunt> liste = new ArrayList<>();
        String query = "SELECT e.*, l.titre, m.nom FROM emprunts e " +
                "JOIN livres l ON e.livre_id = l.id " +
                "JOIN membres m ON e.membre_id = m.id";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Date de = rs.getDate("date_emprunt");
                Date drp = rs.getDate("date_retour_prevue"); // Avec un 'e'
                Date dre = rs.getDate("date_retour_effective"); // Le vrai nom en BDD

                Emprunt emp = new Emprunt(
                        rs.getInt("id"),
                        rs.getInt("livre_id"),
                        rs.getInt("membre_id"),
                        de != null ? de.toLocalDate() : null,
                        drp != null ? drp.toLocalDate() : null,
                        dre != null ? dre.toLocalDate() : null
                );
                emp.setTitreLivre(rs.getString("titre"));
                emp.setNomMembre(rs.getString("nom"));
                liste.add(emp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    /**
     * Enregistre un emprunt et décrémente automatiquement le stock du livre.
     * Utilise une TRANSACTION SQL pour éviter les désynchronisations de données.
     */
    public boolean enregistrerEmprunt(int membreId, int livreId) {
        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement insertStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            // 1. Désactiver l'auto-commit pour démarrer une transaction manuelle
            conn.setAutoCommit(false);

            // 2. Vérifier s'il reste des exemplaires disponibles
            String checkQuery = "SELECT exemplaires_disponibles FROM livres WHERE id = ?";
            checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, livreId);
            rs = checkStmt.executeQuery();

            if (rs.next()) {
                int dispo = rs.getInt("exemplaires_disponibles");
                if (dispo <= 0) {
                    System.out.println(" Impossible : Plus d'exemplaires disponibles !");
                    conn.rollback(); // On annule tout
                    return false;
                }
            } else {
                conn.rollback();
                return false;
            }

            // 3. Insérer l'emprunt (date_retour_prevue)
            String insertQuery = "INSERT INTO emprunts (membre_id, livre_id, date_emprunt, date_retour_prevue) VALUES (?, ?, ?, ?)";
            insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setInt(1, membreId);
            insertStmt.setInt(2, livreId);
            insertStmt.setDate(3, Date.valueOf(LocalDate.now()));
            insertStmt.setDate(4, Date.valueOf(LocalDate.now().plusDays(14)));
            insertStmt.executeUpdate();

            // 4. Décrémenter le stock du livre (-1)
            String updateQuery = "UPDATE livres SET exemplaires_disponibles = exemplaires_disponibles - 1 WHERE id = ?";
            updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setInt(1, livreId);
            updateStmt.executeUpdate();

            // 5. Valider définitivement la transaction si tout est OK
            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println(" Erreur pendant la transaction d'emprunt : " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (checkStmt != null) checkStmt.close();
                if (insertStmt != null) insertStmt.close();
                if (updateStmt != null) updateStmt.close();
                if (conn != null) conn.setAutoCommit(true); // On remet par défaut
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /**
     * Enregistre le retour d'un livre et remet le stock à jour (+1)
     */
    public boolean retournerLivre(int empruntId, int livreId) {
        Connection conn = null;
        PreparedStatement updateEmpruntStmt = null;
        PreparedStatement updateLivreStmt = null;

        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); // Début de la transaction

            // 1. Mettre à jour la date de retour réel de l'emprunt (date_retour_effective)
            String queryEmprunt = "UPDATE emprunts SET date_retour_effective = ? WHERE id = ?";
            updateEmpruntStmt = conn.prepareStatement(queryEmprunt);
            updateEmpruntStmt.setDate(1, Date.valueOf(LocalDate.now()));
            updateEmpruntStmt.setInt(2, empruntId);
            updateEmpruntStmt.executeUpdate();

            // 2. Réaugmenter le stock du livre rendu (+1)
            String queryLivre = "UPDATE livres SET exemplaires_disponibles = exemplaires_disponibles + 1 WHERE id = ?";
            updateLivreStmt = conn.prepareStatement(queryLivre);
            updateLivreStmt.setInt(1, livreId);
            updateLivreStmt.executeUpdate();

            conn.commit(); // Validation
            return true;

        } catch (SQLException e) {
            System.err.println(" Erreur lors du retour du livre : " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            try {
                if (updateEmpruntStmt != null) updateEmpruntStmt.close();
                if (updateLivreStmt != null) updateLivreStmt.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}