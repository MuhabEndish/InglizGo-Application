package com.example.inglizgo_v3;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class QuizScreenController implements Initializable {

    @FXML
    private VBox questionContainer;

    @FXML
    private Label questionLabel;

    @FXML
    private Button option1;

    @FXML
    private Button option2;

    @FXML
    private Button option3;

    @FXML
    private Button option4;

    @FXML
    private Button stopQuizBtn;

    private List<Question> questions;
    private List<Question> incorrectQuestions = new ArrayList<>();
    private Question currentQuestion;
    private int currentQuestionIndex = 0;
    private int correctAnswersInARow = 0;
    private int totalCorrectAnswers = 0;

    private String loggedInUsername;
    private QuizManager quizManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Başlangıçta kullanıcı adı olmadan initialize edilebilir
    }

    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
        this.quizManager = new QuizManager(this.loggedInUsername);
        loadQuestions();
        displayNextQuestion();
    }

    public void loadQuestions() {
        questions = quizManager.fetchQuestionsForUser();
    }

    private void displayNextQuestion() {
        if (currentQuestionIndex < questions.size()) {
            currentQuestion = questions.get(currentQuestionIndex);
            questionLabel.setText("What is the Turkish meaning of "+ currentQuestion.getEnWord() + "?");
            List<String> options = currentQuestion.getShuffledOptions();
            option1.setText(options.get(0));
            option2.setText(options.get(1));
            option3.setText(options.get(2));
            option4.setText(options.get(3));
            resetButtonStyles();
        } else if (!incorrectQuestions.isEmpty()) {
            // Retry incorrect questions
            questions = new ArrayList<>(incorrectQuestions);
            incorrectQuestions.clear();
            currentQuestionIndex = 0;
            displayNextQuestion();
        } else {
            // Sınav bitince yapılacak işlemler
            showQuizResults();
            disableButtons();
        }
    }

    private void resetButtonStyles() {
        option1.setStyle("-fx-background-radius: 20; -fx-background-color: #40a2e3; -fx-text-fill: WHITE;");
        option2.setStyle("-fx-background-radius: 20; -fx-background-color: #40a2e3; -fx-text-fill: WHITE;");
        option3.setStyle("-fx-background-radius: 20; -fx-background-color: #40a2e3; -fx-text-fill: WHITE;");
        option4.setStyle("-fx-background-radius: 20; -fx-background-color: #40a2e3; -fx-text-fill: WHITE;");
    }

    private void disableButtons() {
        option1.setDisable(true);
        option2.setDisable(true);
        option3.setDisable(true);
        option4.setDisable(true);
        stopQuizBtn.setDisable(true);
    }

    @FXML
    private void handleOptionSelected(ActionEvent event) {
        Button selectedButton = (Button) event.getSource();
        String selectedAnswer = selectedButton.getText();

        boolean isCorrect = currentQuestion.getCorrectAnswer().equals(selectedAnswer);

        if (isCorrect) {
            correctAnswersInARow++;
            totalCorrectAnswers++;
            selectedButton.setStyle("-fx-background-color: green;");
            showMotivationalMessage("Correct! Keep going.");
            if (correctAnswersInARow == 6) {
                System.out.println("Question mastered: " + currentQuestion.getQuestion());
                correctAnswersInARow = 0;
            }
        } else {
            correctAnswersInARow = 0;
            selectedButton.setStyle("-fx-background-color: red;");
            showMotivationalMessage("Incorrect. Try again.");
            incorrectQuestions.add(currentQuestion);
        }

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> {
            currentQuestionIndex++;
            displayNextQuestion();
        });
        pause.play();
    }

    private void showMotivationalMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Quiz Feedback");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> alert.close());
        pause.play();
    }

    private void showQuizResults() {
        int totalQuestions = questions.size() + incorrectQuestions.size();
        int score = (totalCorrectAnswers * 100) / totalQuestions;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Quiz Finished");
        alert.setHeaderText(null);
        alert.setContentText("Quiz finished! Your score: " + score + "%");
        alert.showAndWait();

        questionLabel.setText("Quiz finished! Your score: " + score + "%");
    }

    @FXML
    private void stopQuiz(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/inglizgo_v3/mainForm.fxml"));
            Parent mainRoot = loader.load();

            Scene mainScene = new Scene(mainRoot);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(mainScene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
