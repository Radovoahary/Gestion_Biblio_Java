module org.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.gson;

    // Autorise JavaFX à lire les contrôleurs et les fichiers FXML
    opens org.example.demo.controller to javafx.fxml;
    opens org.example.demo to javafx.fxml;

    // Autorise JavaFX (le module javafx.base) à lire les propriétés des modèles (Livre, Membre) pour les tableaux
    opens org.example.demo.model to javafx.base;

    // Autorise GSON à lire les configurations privées
    opens org.example.demo.config to com.google.gson;

    exports org.example.demo;
}