package org.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-view.fxml")); // ou ton fichier principal
        Scene scene = new Scene(fxmlLoader.load(), 900, 650);

        // Injection CSS
        scene.getStylesheets().add(getClass().getResource("/org/example/demo/style.css").toExternalForm());

        stage.setTitle("Library System Management Pro");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}