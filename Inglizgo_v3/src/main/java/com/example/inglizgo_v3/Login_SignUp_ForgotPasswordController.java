package com.example.inglizgo_v3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.*;

public class Login_SignUp_ForgotPasswordController implements Initializable {

    // FXML elements for various forms and controls
    @FXML
    private TextField ForgPass_Email;

    @FXML
    private TextField Forg_Answer;

    @FXML
    private Button Forg_BackBtn;

    @FXML
    private Button Forg_ContinueBtn;

    @FXML
    private ComboBox<?> Forg_SecQuestion;

    @FXML
    private AnchorPane ForgotPass_Form;

    @FXML
    private Button Login_CreatAcc;

    @FXML
    private Hyperlink Login_ForgPass;

    @FXML
    private AnchorPane Login_Form;

    @FXML
    private CheckBox Login_selectShowPass;

    @FXML
    private Button Login_btn;

    @FXML
    private PasswordField Login_password;

    @FXML
    private TextField Login_shownPassword;

    @FXML
    private TextField Login_username;

    @FXML
    private Button ResetPass_Back;

    @FXML
    private PasswordField ResetPass_ConNewPass;

    @FXML
    private AnchorPane ResetPass_Form;

    @FXML
    private PasswordField ResetPass_NewPass;

    @FXML
    private Button ResetPassword_Btn;

    @FXML
    private AnchorPane SignUp_Form;

    @FXML
    private Button SignUp_btn;

    @FXML
    private PasswordField Sign_ConfPass;

    @FXML
    private TextField Sign_Email;

    @FXML
    private Button Sign_LoginBtn;

    @FXML
    private PasswordField Sign_Passw;

    @FXML
    private ComboBox<?> Sign_SecQuestion;

    @FXML
    private TextField Sign_Username;

    @FXML
    private TextField Sign_answer;

    // Database connection variables
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;
    private Statement statement;

    // Method to establish a connection to the MySQL database named "inglizgo_app"
    public Connection connectDB(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/inglizgo_app","root","");
        }
        catch(Exception e){e.printStackTrace();}
        return null;
    }

    // Method to handle user login
    public void Login(){
        AlertMessage alert = new AlertMessage();
        MainFormController mainformcontroller = new MainFormController();

        // Checks if any login fields are empty
        if(Login_username.getText().isEmpty()
                || Login_password.getText().isEmpty()){
            alert.errorMessage("Incorrect Username or Password.");
        } else {
            String selectData = "SELECT user_id , UserName , UserPassword FROM user_info WHERE "
                    + "UserName = ? and UserPassword = ? ";

            connect = connectDB();

            // Syncs the visible password field with the actual password field
            if (Login_selectShowPass.isSelected()){
                Login_password.setText(Login_shownPassword.getText());
            } else {
                Login_shownPassword.setText(Login_password.getText());
            }

            try {
                prepare = connect.prepareStatement(selectData);
                prepare.setString(1, Login_username.getText());
                prepare.setString(2, Login_password.getText());

                result = prepare.executeQuery();
                if (result.next()) {
                    long userId = result.getLong("user_id");
                    System.out.println("Logged in user_id: " + userId);

                    // Proceed to the main form and pass the user_id
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("mainForm.fxml"));
                    Parent root = loader.load();
                    MainFormController mainFormController = loader.getController();
                    mainFormController.setUserId(userId);  // Assuming you have a setter for user_id in MainFormController

                    // Set the logged-in username in MainFormController
                    mainFormController.setLoggedInUsername(Login_username.getText());

                    // Load user image from the database
                    Image userImage = mainFormController.loadUserImageFromDatabase(Login_username.getText());
                    mainFormController.setUserImage(userImage);

                    Stage stage = new Stage();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();  // Show the main form
                    Login_btn.getScene().getWindow().hide();  // Hide the login window

                    alert.successMessage("Successfully Login!");

                } else {
                    alert.errorMessage("Incorrect Username or Password.");
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    // Method to toggle the visibility of the password
    public void showPassword(){
        if(Login_selectShowPass.isSelected()){
            Login_shownPassword.setText(Login_password.getText());
            Login_shownPassword.setVisible(true);
            Login_password.setVisible(false);
        } else {
            Login_password.setText(Login_shownPassword.getText());
            Login_shownPassword.setVisible(false);
            Login_password.setVisible(true);
        }
    }

    // Method to handle forgotten password functionality
    public void forgotPassword(){
        AlertMessage alert = new AlertMessage();

        // Checks if any forgot password fields are empty
        if(ForgPass_Email.getText().isEmpty()
                || Forg_SecQuestion.getSelectionModel().getSelectedItem() == null
                || Forg_Answer.getText().isEmpty()){
            alert.errorMessage("Please fill all fields!");
        } else {
            String checkData = "SELECT UserEmail , SecurityQuestion , SecQueAnswer FROM user_info "
                    + "WHERE UserEmail = ? AND SecurityQuestion = ? AND SecQueAnswer = ? ";

            connect = connectDB();

            try {
                prepare = connect.prepareStatement(checkData);
                prepare.setString(1 , ForgPass_Email.getText());
                prepare.setString(2 , (String) Forg_SecQuestion.getSelectionModel().getSelectedItem());
                prepare.setString(3 , Forg_Answer.getText());

                result = prepare.executeQuery();

                // If user entered valid data
                if (result.next()){
                    // Proceed to change password form
                    SignUp_Form.setVisible(false);
                    Login_Form.setVisible(false);
                    ForgotPass_Form.setVisible(false);
                    ResetPass_Form.setVisible(true);
                } else {
                    alert.errorMessage("Incorrect Information.");
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    // Method to populate security question list for forgot password form
    public void forgotPasswordQuestionList(){
        List<String> listQ = new ArrayList<>();
        Collections.addAll(listQ, questionList);
        ObservableList listData = FXCollections.observableArrayList(listQ);
        Forg_SecQuestion.setItems(listData);
    }

    // Method to reset password
    public void resetPassword(){
        AlertMessage alert = new AlertMessage();

        // Checks if any reset password fields are empty
        if (ResetPass_NewPass.getText().isEmpty() || ResetPass_ConNewPass.getText().isEmpty()){
            alert.errorMessage("Please fill all the fields!");
        } else if (!ResetPass_NewPass.getText().equals(ResetPass_ConNewPass.getText())){
            alert.errorMessage("Password does not match");
        } else if (ResetPass_NewPass.getText().length() < 8) {
            alert.errorMessage("The new Password should be longer than 8 characters.");
        } else {
            String updateData = "UPDATE user_info SET UserPassword = ? "
                    + "WHERE UserEmail = '" + ForgPass_Email.getText() + "'";

            connect = connectDB();

            try {
                prepare = connect.prepareStatement(updateData);
                prepare.setString(1 , ResetPass_NewPass.getText());
                prepare.executeUpdate();

                alert.successMessage("Password changed successfully.");

                // Show login form after password change
                SignUp_Form.setVisible(false);
                Login_Form.setVisible(true);
                ForgotPass_Form.setVisible(false);
                ResetPass_Form.setVisible(false);

                // Clear reset password form
                ResetPass_NewPass.setText("");
                ResetPass_ConNewPass.setText("");

                // Clear forgot password form
                ForgPass_Email.setText("");
                Forg_SecQuestion.getSelectionModel().clearSelection();
                Forg_Answer.setText("");
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    // Method to register a new user
    public void Register(){
        AlertMessage alert = new AlertMessage();

        // Checks if any sign-up fields are empty
        if(Sign_Email.getText().isEmpty() || Sign_Username.getText().isEmpty()
                || Sign_Passw.getText().isEmpty() || Sign_ConfPass.getText().isEmpty()
                || (Sign_SecQuestion.getSelectionModel().getSelectedItem() == null)
                || Sign_answer.getText().isEmpty()){
            alert.errorMessage("All the fields are required.");
        } else if (!Objects.equals(Sign_Passw.getText(), Sign_ConfPass.getText())) {
            alert.errorMessage("Password doesn't match.");
        } else if (Sign_Passw.getText().length() < 8) {
            alert.errorMessage("Invalid Password, at least 8 characters needed.");
        } else {
            // Checks if the username is already taken
            String checkUsername = "SELECT * FROM user_info WHERE UserName = ?";
            connect = connectDB();

            try {
                PreparedStatement checkUsernameStatement = connect.prepareStatement(checkUsername);
                checkUsernameStatement.setString(1, Sign_Username.getText());

                // Use executeQuery() to check if the username exists
                ResultSet result = checkUsernameStatement.executeQuery();

                if (result.next()) {
                    alert.errorMessage(Sign_Username.getText() + " is already taken.");
                } else {
                    String insertData = "INSERT INTO user_info " +
                            "(UserEmail, UserName, UserPassword, SecurityQuestion, SecQueAnswer)"
                            + " VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement prepare = connect.prepareStatement(insertData);

                    // Set parameters for the insert statement
                    prepare.setString(1, Sign_Email.getText());
                    prepare.setString(2, Sign_Username.getText());
                    prepare.setString(3, Sign_Passw.getText());
                    prepare.setString(4, (String) Sign_SecQuestion.getSelectionModel().getSelectedItem());
                    prepare.setString(5, Sign_answer.getText());

                    prepare.executeUpdate();

                    alert.successMessage("Registered successfully!");

                    clearRegisterFields();

                    SignUp_Form.setVisible(false);
                    Login_Form.setVisible(true);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Method to clear fields after registration
    public void clearRegisterFields(){
        Sign_Email.setText("");
        Sign_Username.setText("");
        Sign_Passw.setText("");
        Sign_ConfPass.setText("");
        Sign_SecQuestion.getSelectionModel().clearSelection();
        Sign_answer.setText("");
    }

    // Method to switch between forms based on button clicks
    public void switchBetweenForms(ActionEvent event){
        if (event.getSource() == Sign_LoginBtn || event.getSource() == Forg_BackBtn){
            SignUp_Form.setVisible(false);
            Login_Form.setVisible(true);
            ForgotPass_Form.setVisible(false);
            ResetPass_Form.setVisible(false);
        } else if (event.getSource() == Login_CreatAcc) {
            SignUp_Form.setVisible(true);
            Login_Form.setVisible(false);
            ForgotPass_Form.setVisible(false);
            ResetPass_Form.setVisible(false);

            Login_password.setVisible(true);
            Login_password.setText("");
            Login_shownPassword.setVisible(false);
            Login_selectShowPass.setSelected(false);
        } else if (event.getSource() == Login_ForgPass) {
            SignUp_Form.setVisible(false);
            Login_Form.setVisible(false);
            ForgotPass_Form.setVisible(true);
            ResetPass_Form.setVisible(false);

            Login_password.setVisible(true);
            Login_password.setText("");
            Login_shownPassword.setVisible(false);
            Login_selectShowPass.setSelected(false);

            forgotPasswordQuestionList();
        } else if (event.getSource() == ResetPass_Back) {
            SignUp_Form.setVisible(false);
            Login_Form.setVisible(false);
            ForgotPass_Form.setVisible(true);
            ResetPass_Form.setVisible(false);
        }
    }

    // List of security questions
    private final String[] questionList = { "What is your best friend's name?"
            , "What is your favorite color?"
            , "What is the name of your pet?"
            , "What is your favorite place?"
            , "What is mother's maiden name?"
            , "What was the name of your elementary school?"
            , "What is your best teacher's name?" };

    // Method to populate security question list for sign-up form
    public void SecurityQuestion(){
        List<String> listQ = new ArrayList<>();
        Collections.addAll(listQ, questionList);
        ObservableList listData = FXCollections.observableArrayList(listQ);
        Sign_SecQuestion.setItems(listData);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SecurityQuestion();
        forgotPasswordQuestionList();
    }
}
