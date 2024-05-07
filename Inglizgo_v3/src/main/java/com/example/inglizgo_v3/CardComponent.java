package com.example.inglizgo_v3;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class CardComponent extends BorderPane {
    private WordCard wordCard;

    public CardComponent(WordCard wordCard) {
        this.wordCard = wordCard;
        initializeUI();
    }

    private void initializeUI() {
        // Create an ImageView for the image
        ImageView imageView = new ImageView(wordCard.getwordImage());
        imageView.setFitWidth(200); // Set the width of the image
        imageView.setFitHeight(150); // Set the height of the image

        // Create a Label for the word
        Label wordLabel = new Label(wordCard.getEnglishWord());
        wordLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #ff0000;");
        wordLabel.setPrefWidth(100); // Set the preferred width
        wordLabel.setPrefHeight(50); // Set the preferred height

        // Create a Label for the meaning
        Label translationLabel = new Label("Meaning: " + wordCard.getTranslation());
        translationLabel.setStyle("-fx-font-size: 14px;");

        // Create a Label for the first example
        Label firstExLabel = new Label("First Example: " + wordCard.getFirstExample());
        firstExLabel.setStyle("-fx-font-size: 14px;");

        // Create a Label for the second example
        Label secondExLabel = new Label("Second Example: " + wordCard.getSecondExample());
        secondExLabel.setStyle("-fx-font-size: 14px;");

        // Create a VBox to hold the labels
        VBox CardComponents = new VBox(5); // Spacing between labels
        CardComponents.setPadding(new Insets(10));
        CardComponents.getChildren().addAll(wordLabel, translationLabel, firstExLabel, secondExLabel, imageView);

        // Set the ImageView and VBox to the center and bottom of the BorderPane respectively
        setCenter(CardComponents);

        // Set some padding for the BorderPane
        setPadding(new Insets(10));
    }
}
