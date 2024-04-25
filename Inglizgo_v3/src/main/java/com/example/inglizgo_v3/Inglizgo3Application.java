package com.example.inglizgo_v3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Inglizgo3Application extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Inglizgo3Application.class.getResource("Login_SignUp_ForotPassword.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 650, 500);
        stage.setTitle("Inglizgo v3");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch();

    }
}