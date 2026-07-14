package org.example.demo.config;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Charge la configuration de connexion à la base depuis un fichier JSON
 * situé à LA RACINE DU PROJET (à côté de pom.xml), et non plus depuis les
 * ressources packagées : ceci évite les problèmes de copie Maven vers
 * target/classes rencontrés précédemment.
 *
 * Placez votre fichier ici : demo/db_config.json (au même niveau que pom.xml)
 */
public class DatabaseConfig {

    private static final String NOM_FICHIER = "db_config.json";

    private static DbCredentials credentials;

    static {
        chargerConfiguration();
    }

    private static void chargerConfiguration() {
        Path chemin = Paths.get(NOM_FICHIER).toAbsolutePath();

        if (!Files.exists(chemin)) {
            System.err.println("❌ Fichier de configuration introuvable : " + chemin);
            System.err.println("   Créez un fichier '" + NOM_FICHIER + "' à la racine du projet (à côté de pom.xml).");
            return;
        }

        try (Reader reader = new FileReader(chemin.toFile())) {
            credentials = new Gson().fromJson(reader, DbCredentials.class);
            System.out.println("✅ Configuration de la base chargée depuis : " + chemin);
        } catch (IOException e) {
            System.err.println("❌ Erreur de lecture de " + chemin + " : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (credentials == null || credentials.url == null) {
            throw new SQLException("Configuration de la base de données absente ou invalide ("
                    + Paths.get(NOM_FICHIER).toAbsolutePath() + "). Vérifiez que le fichier existe à la racine du projet.");
        }
        if (credentials.user == null) {
            throw new SQLException("Le champ 'user' est manquant dans " + NOM_FICHIER + ".");
        }
        if (credentials.password == null) {
            throw new SQLException("Le champ 'password' est manquant dans " + NOM_FICHIER + " (mettre \"\" si pas de mot de passe).");
        }
        return DriverManager.getConnection(credentials.url, credentials.user, credentials.password);
    }

    /** Classe interne utilisée uniquement pour le mapping JSON via Gson. */
    private static class DbCredentials {
        String url;
        String user;
        String password;
    }
}
