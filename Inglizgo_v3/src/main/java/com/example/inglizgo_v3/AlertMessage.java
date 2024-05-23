package com.example.inglizgo_v3;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class AlertMessage {

    private Alert alert; // Instance of Alert

    // Method to display an error message
    public void errorMessage(String message) {
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error message");
        alert.setHeaderText(null); // No header text
        alert.setContentText(message); // Set the content of the alert
        alert.showAndWait(); // Display the alert and wait for user action
    }

    // Method to display a success message
    public void successMessage(String message) {
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information message");
        alert.setHeaderText(null); // No header text
        alert.setContentText(message); // Set the content of the alert
        alert.showAndWait(); // Display the alert and wait for user action
    }

    // Method to display a confirmation message and return the user's response
    public Optional<ButtonType> confirmationMessage(String message) {
        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation message");
        alert.setHeaderText(null); // No header text
        alert.setContentText(message); // Set the content of the alert
        return alert.showAndWait(); // Display the alert and wait for user action, returning the result
    }
}
