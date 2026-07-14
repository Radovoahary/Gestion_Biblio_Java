package org.example.demo.dao;

import org.example.demo.config.DatabaseConfig;
import org.example.demo.model.Emprunt;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmpruntDAO {

    // RÈGLE MÉTIER : quotas d'emprunts simultanés selon le type de membre
    private static final int LIMITE_ETUDIANT = 3;
    private static final int LIMITE_ENSEIGNANT = 6;

    private String dernierErreur;

    public String getDernierErreur() {
        return dernierErreur;
    }

    // CREATE : Enregistrer un emprunt avec gestion transactionnelle du stock
    // + vérification du quota d'emprunts actifs du membre.
    public boolean enregistrerEmprunt(int idLivre, int idMembre) {
        String checkMembreQuery = "SELECT type_membre FROM membres WHERE id = ?";
        String checkEmpruntsActifsQuery = "SELECT COUNT(*) FROM emprunts WHERE membre_id = ? AND date_retour_reel IS NULL";
        String checkStockQuery = "SELECT exemplaires_disponibles FROM livres WHERE id = ?";
        String insertEmpruntQuery = "INSERT INTO emprunts (livre_id, membre_id, date_emprunt, date_retour_prevu) VALUES (?, ?, ?, ?)";
        String updateStockQuery = "UPDATE livres SET exemplaires_disponibles = exemplaires_disponibles - 1 WHERE id = ?";

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); // Début de la transaction

            // 1. Récupérer le type du membre (nécessaire pour connaître son quota)
            String typeMembre;
            try (PreparedStatement stmtMembre = conn.prepareStatement(checkMembreQuery)) {
                stmtMembre.setInt(1, idMembre);
                try (ResultSet rs = stmtMembre.executeQuery()) {
                    if (!rs.next()) {
                        dernierErreur = "Aucun membre ne correspond à cet identifiant.";
                        conn.rollback();
                        return false;
                    }
                    typeMembre = rs.getString("type_membre");
                }
            }

            // 2. RÈGLE MÉTIER : vérifier que le membre n'a pas atteint son quota
            //    d'emprunts simultanés (3 pour un étudiant, 6 pour un enseignant)
            int quotaMax = "ENSEIGNANT".equalsIgnoreCase(typeMembre) ? LIMITE_ENSEIGNANT : LIMITE_ETUDIANT;
            int empruntsActifs;
            try (PreparedStatement stmtCount = conn.prepareStatement(checkEmpruntsActifsQuery)) {
                stmtCount.setInt(1, idMembre);
                try (ResultSet rs = stmtCount.executeQuery()) {
                    rs.next();
                    empruntsActifs = rs.getInt(1);
                }
            }
            if (empruntsActifs >= quotaMax) {
                dernierErreur = "Quota d'emprunts atteint : ce membre (" + typeMembre + ") a déjà "
                        + empruntsActifs + " emprunt(s) en cours, pour un maximum autorisé de " + quotaMax + ".";
                conn.rollback();
                return false;
            }

            // 3. Vérification du stock
            try (PreparedStatement stmtCheck = conn.prepareStatement(checkStockQuery)) {
                stmtCheck.setInt(1, idLivre);
                try (ResultSet rs = stmtCheck.executeQuery()) {
                    if (!rs.next()) {
                        dernierErreur = "Aucun livre ne correspond à cet identifiant.";
                        conn.rollback();
                        return false;
                    }
                    if (rs.getInt("exemplaires_disponibles") <= 0) {
                        dernierErreur = "Aucun exemplaire disponible pour ce livre.";
                        conn.rollback();
                        return false;
                    }
                }
            }

            // 4. Insertion de l'emprunt (Durée par défaut : 14 jours)
            LocalDate aujourdHui = LocalDate.now();
            LocalDate retourPrevu = aujourdHui.plusDays(14);
            try (PreparedStatement stmtInsert = conn.prepareStatement(insertEmpruntQuery)) {
                stmtInsert.setInt(1, idLivre);
                stmtInsert.setInt(2, idMembre);
                stmtInsert.setDate(3, Date.valueOf(aujourdHui));
                stmtInsert.setDate(4, Date.valueOf(retourPrevu));
                stmtInsert.executeUpdate();
            }

            // 5. Diminution du stock du livre
            try (PreparedStatement stmtUpdate = conn.prepareStatement(updateStockQuery)) {
                stmtUpdate.setInt(1, idLivre);
                stmtUpdate.executeUpdate();
            }

            conn.commit(); // Validation finale
            return true;
        } catch (SQLException e) {
            dernierErreur = "Erreur lors de l'enregistrement de l'emprunt (ID membre invalide ?) : " + e.getMessage();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Nombre d'emprunts actuellement en cours (non retournés) pour un membre.
     * Exposé publiquement : réutilisable pour l'afficher dans l'écran Membres,
     */
    public int compterEmpruntsActifs(int idMembre) {
        String query = "SELECT COUNT(*) FROM emprunts WHERE membre_id = ? AND date_retour_reel IS NULL";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idMembre);
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

    // READ : Récupérer tous les emprunts actifs (non encore retournés officiellement)
    public List<Emprunt> getAllEmprunts() {
        List<Emprunt> liste = new ArrayList<>();
        String query = "SELECT e.*, l.titre AS titre_livre, m.nom AS nom_membre " +
                "FROM emprunts e " +
                "JOIN livres l ON e.livre_id = l.id " +
                "JOIN membres m ON e.membre_id = m.id " +
                "WHERE e.date_retour_reel IS NULL"; // Filtre uniquement ceux en cours

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Date effectiveDateSql = rs.getDate("date_retour_reel");
                LocalDate dateEffective = (effectiveDateSql != null) ? effectiveDateSql.toLocalDate() : null;

                Emprunt emp = new Emprunt(
                        rs.getInt("id"),
                        rs.getInt("livre_id"),
                        rs.getInt("membre_id"),
                        rs.getDate("date_emprunt").toLocalDate(),
                        rs.getDate("date_retour_prevu").toLocalDate(),
                        dateEffective
                );
                emp.setTitreLivre(rs.getString("titre_livre"));
                emp.setNomMembre(rs.getString("nom_membre"));
                liste.add(emp);
            }
        } catch (SQLException e) {
            dernierErreur = "Erreur lors du chargement des emprunts : " + e.getMessage();
            e.printStackTrace();
        }
        return liste;
    }

    // UPDATE : Marquer un retour de livre (+ ré-augmentation du stock)
    public boolean retournerLivre(int idLivre, int idMembre) {
        String updateEmpruntQuery = "UPDATE emprunts SET date_retour_reel = ? WHERE livre_id = ? AND membre_id = ? AND date_retour_reel IS NULL";
        String updateStockQuery = "UPDATE livres SET exemplaires_disponibles = exemplaires_disponibles + 1 WHERE id = ?";

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmtEmp = conn.prepareStatement(updateEmpruntQuery)) {
                stmtEmp.setDate(1, Date.valueOf(LocalDate.now()));
                stmtEmp.setInt(2, idLivre);
                stmtEmp.setInt(3, idMembre);
                if (stmtEmp.executeUpdate() == 0) {
                    dernierErreur = "Aucun emprunt actif ne correspond à ce livre et ce membre.";
                    conn.rollback();
                    return false;
                }
            }

            try (PreparedStatement stmtStock = conn.prepareStatement(updateStockQuery)) {
                stmtStock.setInt(1, idLivre);
                stmtStock.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            dernierErreur = "Erreur lors de l'enregistrement du retour : " + e.getMessage();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // DELETE : Supprimer une fiche d'emprunt définitivement
    public boolean supprimerEmprunt(int id) {
        String query = "DELETE FROM emprunts WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            dernierErreur = "Erreur lors de la suppression de la fiche : " + e.getMessage();
            e.printStackTrace();
            return false;
        }
    }
}
