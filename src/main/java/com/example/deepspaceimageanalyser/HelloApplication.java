package com.example.deepspaceimageanalyser;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("MAINSPACE.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 750, 737);
        Image image = new Image("https://png.pngtree.com/png-vector/20190215/ourlarge/pngtree-vector-space-icon-png-image_516436.jpg");
        stage.setTitle("Deep Space Image Analyser");
        stage.getIcons().add(image);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        FileChooser fileChooser = new FileChooser();
        launch();
    }
}