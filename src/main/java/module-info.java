module org.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;          // Indispensable pour la connexion MySQL / JDBC
    requires com.google.gson;     // Indispensable pour lire le fichier JSON de config

    // Permet à JavaFX de lire tes contrôleurs graphiques
    opens org.example.demo to javafx.fxml;

   exports org.example.demo;
}