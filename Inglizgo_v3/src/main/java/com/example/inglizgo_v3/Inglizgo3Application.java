package com.example.inglizgo_v3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Inglizgo3Application extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Inglizgo3Application.class.getResource("Login_SignUp_ForotPassword.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 650);
        stage.setTitle("Inglizgo");
        stage.setScene(scene);

//        // Load and set the application icon
//        Image icon = new Image("");
//        // Replace "path_to_your_icon.png" with the path to your icon
//        stage.getIcons().add(icon);

        stage.show();

    }


    public static void main(String[] args) {
        launch();

    }
}
