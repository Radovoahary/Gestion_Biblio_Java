module org.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.gson;

    // Autorise JavaFX à lire les contrôleurs graphiques
    opens org.example.demo.controller to javafx.fxml;
    opens org.example.demo to javafx.fxml;

    exports org.example.demo;
}