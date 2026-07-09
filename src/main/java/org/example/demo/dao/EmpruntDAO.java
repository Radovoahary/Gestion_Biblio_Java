package org.example.demo.dao;

import org.example.demo.config.DatabaseConfig;
import org.example.demo.model.Emprunt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpruntDAO {

    public List<Emprunt> getAllEmprunts() {
        List<Emprunt> liste = new ArrayList<>();
        String query = "SELECT e.*, l.titre, m.nom FROM emprunts e " +
                "JOIN livres l ON e.id_livre = l.id " +
                "JOIN membres m ON e.id_membre = m.id";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Date de = rs.getDate("date_emprunt");
                Date drp = rs.getDate("date_retour_prevue");
                Date dre = rs.getDate("date_retour_effective");

                Emprunt emp = new Emprunt(
                        rs.getInt("id"),
                        rs.getInt("id_livre"),
                        rs.getInt("id_membre"),
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

    public boolean enregistrerEmprunt(int idLivre, int idMembre) {
        String query = "INSERT INTO emprunts (id_livre, id_membre, date_emprunt, date_retour_prevue) VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY))";
        String updateLivre = "UPDATE livres SET exemplaires_disponibles = exemplaires_disponibles - 1 WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false); // Début transaction

            try (PreparedStatement stmtInsert = conn.prepareStatement(query);
                 PreparedStatement stmtUpdate = conn.prepareStatement(updateLivre)) {

                stmtInsert.setInt(1, idLivre);
                stmtInsert.setInt(2, idMembre);
                stmtInsert.executeUpdate();

                stmtUpdate.setInt(1, idLivre);
                stmtUpdate.executeUpdate();

                conn.commit(); // Valide l'emprunt et la baisse de stock simultanément
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}