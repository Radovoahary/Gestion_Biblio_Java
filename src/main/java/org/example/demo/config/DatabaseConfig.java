package org.example.demo.config;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    private static DbCredentials credentials;

    static {
        chargerConfiguration();
    }

    private static void chargerConfiguration() {
        try (InputStream is = DatabaseConfig.class.getResourceAsStream("/org/example/demo/config/db-config.json")) {
            if (is == null) {
                throw new IllegalStateException(
                        "Fichier db-config.json introuvable dans les ressources (org/example/demo/config/).");
            }
            try (Reader reader = new InputStreamReader(is)) {
                credentials = new Gson().fromJson(reader, DbCredentials.class);
            }
        } catch (Exception e) {
            System.err.println(" Impossible de charger la configuration de la base de données : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (credentials == null || credentials.url == null) {
            throw new SQLException("Configuration de la base de données absente ou invalide (db-config.json).");
        }
        return DriverManager.getConnection(credentials.url, credentials.user, credentials.password);
    }

    /** Classe interne utilisée uniquement pour le mapping JSON via Gson.
     *  Les noms des champs doivent correspondre exactement aux clés du JSON. */
    private static class DbCredentials {
        String url;
        String user;
        String password;
    }
}