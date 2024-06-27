package org.example.algovisualiser;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class VisualAlgo extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        File file = new File("src/main/resources/org/example/algovisualiser/hello-view.fxml");
        Scene scene = new Scene(FXMLLoader.load(file.toURI().toURL()), 1200, 800);
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("VA-Logo.png")));
        stage.getIcons().add(icon);
        stage.setTitle("VisualAlgo");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}