package com.example.inglizgo_v3;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.sql.*;

//public class RoundedImageView {

    // Establishing a connection to the database
    //private Connection connect = connectDB();



    /*public void start(Stage primaryStage) {
        try {
            // Query to retrieve the image path from the database
            String query = "SELECT image_path FROM your_table_name WHERE condition = ?";
            PreparedStatement preparedStatement = connect.prepareStatement(query);
            preparedStatement.setString(1, "your_condition_value");
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String imagePath = resultSet.getString("image_path");

                // Load the image dynamically from the retrieved path
                Image image = new Image(imagePath);

                // Create an ImageView to display the image
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(215); // Set the width of the image view
                imageView.setPreserveRatio(true); // Preserve the aspect ratio of the image

                // Create a Rectangle with rounded corners, matching the size of the ImageView
                Rectangle clip = new Rectangle();
                clip.setWidth(imageView.getFitWidth());
                clip.setHeight(imageView.getFitHeight());
                clip.setArcWidth(20); // Set the horizontal radius for the rounded corners
                clip.setArcHeight(20); // Set the vertical radius for the rounded corners

                // Set the Rectangle as the clip for the ImageView
                imageView.setClip(clip);

                // Create a StackPane to hold the ImageView
                StackPane root = new StackPane(imageView);

                // Create a Scene
                Scene scene = new Scene(root, 300, 250);

                // Set the Scene to the Stage
                primaryStage.setScene(scene);

                // Set the title of the Stage
                primaryStage.setTitle("Rounded ImageView Example");

                // Show the Stage
                primaryStage.show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to establish a connection to the MySQL database
    public Connection connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(
                    "jdbc:mysql://localhost/inglizgo", "root", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
*/