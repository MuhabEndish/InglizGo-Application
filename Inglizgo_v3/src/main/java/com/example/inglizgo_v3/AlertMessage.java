package com.example.inglizgo_v3;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class AlertMessage {


    private Alert alert;

    public void errorMessage(String message){

        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

    }

    public void successMessage(String message){

        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Optional<ButtonType> confirmationMessage(String message) {
        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }

}
