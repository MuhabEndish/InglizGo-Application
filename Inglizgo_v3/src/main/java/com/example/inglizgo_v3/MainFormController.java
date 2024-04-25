package com.example.inglizgo_v3;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class MainFormController implements Initializable {

    @FXML
    private AnchorPane Exam_page;

    @FXML
    private Button HomePage_AddCardBtn;

    @FXML
    private AnchorPane HomePage_WordsScreen;

    @FXML
    private AnchorPane Home_Page;

    @FXML
    private StackPane Program_StackPane;

    @FXML
    private AnchorPane UserInfo_pane;

    @FXML
    private TextField WordCard_FirstEx;

    @FXML
    private TextField WordCard_SecondEx;

    @FXML
    private TextField WordCard_Translate;

    @FXML
    private TextField WordCard_addWord;

    @FXML
    private Button WordCard_uploadImageBtn;

    @FXML
    private ImageView WordCard_uploadedImageView;

    @FXML
    private AnchorPane WordScreenTopPane;

    @FXML
    private AnchorPane lowerPane_WordCard;

    @FXML
    private AnchorPane mainForm_AddCardPane;

    @FXML
    private VBox WordCard_Container;

    @FXML
    private AnchorPane mainForm_mainPane;

    @FXML
    private AnchorPane mainForm_upperPane;


    @FXML
    private Button upperPane_HomeBtn;

    @FXML
    private Button upperPane_QuestionBtn;

    @FXML
    private Button upperPane_SettingsBtn;

    @FXML
    private Label upperPane_logo;

    @FXML
    private Label upperPane_userName;

    @FXML
    private Button userInfo_ChangePasswordBtn;

    @FXML
    private Button userInfo_DeleteAccountBtn;

    @FXML
    private Button userInfo_EditEmailBtn;

    @FXML
    private Button userInfo_EditUserNameBtn;

    @FXML
    private Label userInfo_Email;

    @FXML
    private ImageView userInfo_ImageView;

    @FXML
    private Rectangle userInfo_Rectangle;

    @FXML
    private Label userInfo_UserName;

    @FXML
    private Button userInfo_changeImage;


    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;
    private Statement statement;
    private String loggedInUsername; // Assuming username is used as the identifier

    //METHOD ESTABLISHES A CONNECTION TO A MySQL DATABASE NAMED "inglizgo" HOSTED ON THE LOCALHOST SERVER
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





    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
        updateUsernameLabel();
    }

    // Method to update the label text with the loggedInUsername
    private void updateUsernameLabel() {
        if (loggedInUsername != null) {
            upperPane_userName.setText("Welcome, " + loggedInUsername);
        } else {
            upperPane_userName.setText("Not logged in"); // Or any other default text you want to display
        }
    }

    public void switchBetweenHomeExamSettings(ActionEvent event) {

        //LOGIN FORM WILL BE VISIBLE IF YOU CLICKED ON LOGIN BUTTON IN SIGN UP FORM
        if (event.getSource() == upperPane_HomeBtn) {

            Home_Page.setVisible(true);
            UserInfo_pane.setVisible(false);
            mainForm_AddCardPane.setVisible(false);
            Exam_page.setVisible(false);

        }
        //SIGN UP FORM WILL BE VISIBLE IF YOU CLICKED ON CREATE ACCOUNT IN LOGIN FORM
        else if (event.getSource() == upperPane_SettingsBtn) {
            UserInfo_pane.setVisible(true);
            Home_Page.setVisible(false);
            mainForm_AddCardPane.setVisible(false);
            Exam_page.setVisible(false);
        } else if (event.getSource() == upperPane_QuestionBtn) {
            Home_Page.setVisible(false);
            UserInfo_pane.setVisible(false);
            mainForm_AddCardPane.setVisible(false);
            Exam_page.setVisible(true);
        } else if (event.getSource() == HomePage_AddCardBtn) {
            Home_Page.setVisible(false);
            UserInfo_pane.setVisible(false);
            mainForm_AddCardPane.setVisible(true);
            Exam_page.setVisible(false);

        }
    }



    // Method to open a file dialog and return the selected file
    private File openFileDialog(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        // Show the file chooser dialog
        Stage stage = (Stage) WordCard_uploadImageBtn.getScene().getWindow();
        return fileChooser.showOpenDialog(stage);
    }

    @FXML
    void chooseImage(ActionEvent event) {
        File selectedFile = openFileDialog("Choose an image file");

        // If a file is selected, update the appropriate ImageView with the chosen image
        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            if (event.getSource() == userInfo_changeImage || event.getSource() == userInfo_ImageView) {
                userInfo_ImageView.setImage(image);
            } else if (event.getSource() == WordCard_uploadImageBtn) {
                WordCard_uploadedImageView.setImage(image);
            }
        }
    }


    @FXML
    private void saveToDatabase(ActionEvent event) {
        AlertMessage alert = new AlertMessage();

        if (WordCard_addWord.getText().isEmpty() || WordCard_Translate.getText().isEmpty()
                || WordCard_FirstEx.getText().isEmpty() || WordCard_SecondEx.getText().isEmpty()) {

            alert.errorMessage("Please fill all the fields");
        } else {
            connect = connectDB();

            try {
                // Convert image to bytes
                byte[] imageBytes = null;
                Image image = WordCard_uploadedImageView.getImage();
                if (image != null) {
                    imageBytes = convertImageToBytes(image);
                }

                // Save data to database
                PreparedStatement prepare = connect.prepareStatement("INSERT INTO wordcard (EN_word, TR_translate, FirstEx, SecondEX, UserName, Word_Image ) "
                        + "VALUES (?, ?, ?, ?, ?, ? )");
                prepare.setString(1, WordCard_addWord.getText());
                prepare.setString(2, WordCard_Translate.getText());
                prepare.setString(3, WordCard_FirstEx.getText());
                prepare.setString(4, WordCard_SecondEx.getText());
                prepare.setString(5, loggedInUsername);
                prepare.setBytes(6, imageBytes);
                prepare.executeUpdate();

                alert.successMessage("Card successfully added.");

                WordCard_addWord.setText("");
                WordCard_Translate.setText("");
                WordCard_FirstEx.setText("");
                WordCard_SecondEx.setText("");
                WordCard_uploadedImageView.setImage(null);

                // Load and display all WordCards after saving
                //updateDisplayedWordCards();
            } catch (SQLException | IOException e) {
                e.printStackTrace();
                alert.errorMessage("Error occurred while saving the card.");
            }
        }
    }

    // Method to convert JavaFX Image to byte array
    private byte[] convertImageToBytes(Image image) throws IOException {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }



    /*private List<MyWordCard> wordCards = new ArrayList<>();
    // Method to retrieve word cards from the database
    private void retrieveWordCardsFromDB() {
        connect = connectDB();
        try {
            Statement statement = connect.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM wordcard WHERE UserName = '" + loggedInUsername + "'");
            wordCards.clear(); // Clear the existing list
            while (resultSet.next()) {
                // Create WordCard objects from the retrieved data and add them to the list
                MyWordCard wordCard = new MyWordCard(
                        resultSet.getString("EN_word"),
                        resultSet.getString("TR_translate"),
                        resultSet.getString("FirstEx"),
                        resultSet.getString("SecondEX")
                        // You can add more properties if needed
                );
                wordCards.add(wordCard);
            }
            displayWordCards(); // Display the retrieved word cards
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to display the word cards in your JavaFX application
    private void displayWordCards() {
        // Clear existing content
        WordCard_Container.getChildren().clear();
        // Iterate through the word cards and create UI elements to display them
        for (MyWordCard wordCard : wordCards) {
            // Create UI elements (e.g., labels) for each word card and add them to WordCard_Container
            // You can use any layout you prefer (e.g., GridPane, VBox, HBox)
            // Example:
            Label label = new Label(wordCard.getEN_word() + " - "
                    + wordCard.getTR_translate() + " - " + wordCard.getFirstEx() + " - " + wordCard.getSecondEx());
            WordCard_Container.getChildren().add(label);
        }
    }

    // Call this method after any update to refresh the displayed word cards
    public void updateDisplayedWordCards() {
        retrieveWordCardsFromDB(); // Retrieve word cards from the database
    }*/


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Program_StackPane.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

    }
}