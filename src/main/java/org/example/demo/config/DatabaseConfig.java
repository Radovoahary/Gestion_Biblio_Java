package org.example.demo.config;
import com.google.gson.Gson;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Connection;
import java.sql.DriverManager;
public class DatabaseConfig {
    private String url;
    private String user;
    private String password;

    // Stocke l'instance unique de connexion (Pattern Singleton)
    private static Connection connection = null;

    /* Lecture du fichier JSON et retourne une connexion active.*/
    public static Connection getConnection()
    {
        try {
            if (connection != null && !connection.isClosed())
            {
                return connection;
            }
            //Utilisation de Gson pour lecture du fichier JSON
            Gson gson = new Gson();
            try (FileReader reader = new FileReader("db_config.json"))
            {
                //Transformation JSON en DatabaseConfig
                DatabaseConfig config = gson.fromJson(reader, DatabaseConfig.class);

                //Connection à la base de donnée via drive JDBC
                connection = DriverManager.getConnection(config.url, config.user, config.password);
            }
        }
        catch (Exception e)
        {
            System.out.println("Erreur de connexion BDD : " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }
}
