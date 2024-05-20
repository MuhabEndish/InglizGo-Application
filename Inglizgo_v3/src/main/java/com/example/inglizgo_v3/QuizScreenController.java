package com.example.inglizgo_v3;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
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
    private Button option1, option2, option3, option4, stopQuizBtn;

    private List<Question> questions;
    private Question currentQuestion;
    private int currentQuestionIndex = 0;
    private int correctAnswersInARow = 0;
    private int totalCorrectAnswers = 0;
    private int totalIncorrectAnswers = 0;

    private String loggedInUsername;
    private QuizManager quizManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Placeholder for potential future initialization code
    }

    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
        this.quizManager = new QuizManager(loggedInUsername);
        loadQuestions();
    }

    public void loadQuestions() {
        questions = quizManager.fetchQuestionsForUser();
        displayNextQuestion();
    }



    private void updateQuestionDisplay() {
        questionLabel.setText("What is the Turkish meaning of " + currentQuestion.getEnWord() + " ?");
        List<String> options = currentQuestion.getShuffledOptions();
        option1.setText(options.get(0));
        option2.setText(options.get(1));
        option3.setText(options.get(2));
        option4.setText(options.get(3));
    }

    private void resetButtonStyles() {
        option1.setStyle("-fx-background-radius: 20; -fx-background-color: #40a2e3;");
        option2.setStyle("-fx-background-radius: 20; -fx-background-color: #40a2e3;");
        option3.setStyle("-fx-background-radius: 20; -fx-background-color: #40a2e3;");
        option4.setStyle("-fx-background-radius: 20; -fx-background-color: #40a2e3;");
    }

    @FXML
    private void handleOptionSelected(ActionEvent event) {
        Button selectedButton = (Button) event.getSource();
        boolean isCorrect = currentQuestion.getCorrectAnswer().equals(selectedButton.getText());

        processAnswer(selectedButton, isCorrect);
        currentQuestionIndex++;
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(e -> displayNextQuestion());
        if (currentQuestionIndex >= questions.size()) {
            showQuizResults();
        }
        pause.play();
    }



    private void processAnswer(Button selectedButton, boolean isCorrect) {
        int userId = quizManager.getUserIdFromUsername(loggedInUsername);
        if (isCorrect) {
            correctAnswersInARow++;
            totalCorrectAnswers++;
            selectedButton.setStyle("-fx-background-color: green;");
            if (correctAnswersInARow == 6) {
                quizManager.moveWordToKnownPool(currentQuestion.getWordId());
            }
            quizManager.updateAttempt(userId, currentQuestion.getWordId(), true, correctAnswersInARow, quizManager.getNextReviewDate(correctAnswersInARow));
        } else {
            correctAnswersInARow = 0;
            totalIncorrectAnswers++;
            selectedButton.setStyle("-fx-background-color: red;");
            quizManager.resetWordProgress(currentQuestion.getWordId());
        }
        quizManager.handleUserAnswer(userId, currentQuestion.getWordId(), isCorrect);
    }

    @FXML
    private Label resultLabel;

    private void showQuizResults() {
        int totalQuestions = totalCorrectAnswers + totalIncorrectAnswers;
        int score = (int) (((double) totalCorrectAnswers / totalQuestions) * 100);
        resultLabel.setText(String.format("Results: %d Correct, %d Incorrect, Score: %d%%", totalCorrectAnswers, totalIncorrectAnswers, score));

        // Optionally show performance review
        showPerformanceReview(quizManager.getUserIdFromUsername(loggedInUsername));
    }

    private void displayNextQuestion() {
        if (currentQuestionIndex < questions.size()) {
            currentQuestion = questions.get(currentQuestionIndex);
            updateQuestionDisplay();
            resetButtonStyles();
        } else {
            showQuizResults();
            disableButtons();
        }
    }

    private void finishQuiz() {
        disableButtons();
        showQuizResults();
    }
    private void disableButtons() {
        option1.setDisable(true);
        option2.setDisable(true);
        option3.setDisable(true);
        option4.setDisable(true);
    }

    @FXML
    private Label correctCountLabel, incorrectCountLabel;

    private void updateCountLabels() {
        correctCountLabel.setText("Correct: " + totalCorrectAnswers);
        incorrectCountLabel.setText("Incorrect: " + totalIncorrectAnswers);
    }
    public void showPerformanceReview(int userId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PerformanceReview.fxml"));
            Parent root = loader.load();

            PerformanceReviewController controller = loader.getController();
            controller.init(loggedInUsername);

            Stage stage = new Stage();
            stage.setTitle("Performance Review");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void stopQuiz() {
        Stage stage = (Stage) stopQuizBtn.getScene().getWindow();
        stage.close();
    }




}
