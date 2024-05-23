package com.example.inglizgo_v3;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class QuizScreenController implements Initializable {
    @FXML
    private TextField newQuestions_textField;

    @FXML
    private Button option1;

    @FXML
    private Button option2;

    @FXML
    private Button option3;

    @FXML
    private Button option4;

    @FXML
    private VBox questionContainer;

    @FXML
    private Label questionLabel;

    @FXML
    private AnchorPane quizPane_questions;

    @FXML
    private Button quizScreen_ViewResultsBtn;

    @FXML
    private Label resultLabel;

    @FXML
    private Button stopQuizBtn;

    private List<Question> questions;
    private Question currentQuestion;
    private int currentQuestionIndex = 0;
    private int correctAnswersInARow = 0;
    private int totalCorrectAnswers = 0;
    private int totalIncorrectAnswers = 0;

    private String loggedInUsername;
    private QuizManager quizManager;

    private Map<Integer, PerformanceData> performanceDataMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Placeholder for potential future initialization code
    }

    public void setLoggedInUsername(String UserName) {
        this.loggedInUsername = UserName;
        this.quizManager = new QuizManager(loggedInUsername);
        loadQuestions();
    }

    public void loadQuestions() {
        questions = quizManager.fetchQuestionsForUser();

        if (questions.isEmpty()) {
            questionLabel.setStyle(" -fx-wrap-text:true; -fx-alignment:center; -fx-font-size:22px");
            questionLabel.setText("There are no questions for today. Try to add new words.");
            disableButtons();
            quizScreen_ViewResultsBtn.setDisable(false);
        } else {
            displayNextQuestion();
        }
    }

    private void updateQuestionDisplay() {
        if (currentQuestion != null) {
            questionLabel.setText("What is the Turkish meaning of " + currentQuestion.getEnWord() + " ?");
            List<String> options = currentQuestion.getShuffledOptions();
            option1.setText(options.get(0));
            option2.setText(options.get(1));
            option3.setText(options.get(2));
            option4.setText(options.get(3));
        } else {
            questionLabel.setText("There are no other questions for today.");
            quizScreen_ViewResultsBtn.setDisable(false);
        }
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
            handleQuizCompletion();
        }
        pause.play();
    }

    private void processAnswer(Button selectedButton, boolean isCorrect) {
        String UserName = loggedInUsername;
        int wordId = currentQuestion.getWordId();
        LocalDateTime lastAttemptDate = LocalDateTime.now();

        PerformanceData data = performanceDataMap.getOrDefault(wordId, new PerformanceData(
                UserName, wordId, currentQuestion.getEnWord(), 0, 0, lastAttemptDate, 0, LocalDateTime.now(), 0));

        boolean shouldReset = !isCorrect || data.getNextReviewDate().isBefore(LocalDateTime.now());

        if (shouldReset) {
            correctAnswersInARow = 0;
            totalIncorrectAnswers++;
            int repetition = 1;
            LocalDateTime nextReviewDate = LocalDateTime.now().plusDays(1);
            data.setIncorrectAnswers(data.getIncorrectAnswers() + 1);
            data.setRepetition(repetition);
            data.setNextReviewDate(nextReviewDate);
            quizManager.handleUserAnswer(UserName, wordId, false);
            selectedButton.setStyle("-fx-background-color: red;  -fx-background-radius: 20px");
            quizManager.updateAttempt(UserName, wordId, false, repetition, nextReviewDate);
        } else {
            correctAnswersInARow++;
            totalCorrectAnswers++;
            int repetition = data.getRepetition() + 1;
            LocalDateTime nextReviewDate = quizManager.getNextReviewDate(repetition);
            data.setCorrectAnswers(data.getCorrectAnswers() + 1);
            data.setRepetition(repetition);
            data.setNextReviewDate(nextReviewDate);
            quizManager.handleUserAnswer(UserName, wordId, true);
            selectedButton.setStyle("-fx-background-color: green; -fx-background-radius: 20px");
            if (repetition > 6) {
                quizManager.moveWordToKnownPool(wordId);
            } else {
                quizManager.updateAttempt(UserName, wordId, true, repetition, nextReviewDate);
            }
        }

        data.setTotalAttempts(data.getCorrectAnswers() + data.getIncorrectAnswers());
        data.setLastAttemptDate(lastAttemptDate);

        performanceDataMap.put(wordId, data);
    }

    private List<PerformanceData> collectPerformanceData() {
        return new ArrayList<>(performanceDataMap.values());
    }

    @FXML
    private void handleQuizCompletion() {
        try {
            List<PerformanceData> performanceDataList = collectPerformanceData();
            quizManager.insertUserAttempts(performanceDataList);
            showQuizResults();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showQuizResults() {
        int totalQuestions = totalCorrectAnswers + totalIncorrectAnswers;
        int score = (int) (((double) totalCorrectAnswers / totalQuestions) * 100);
        resultLabel.setText(String.format("Results: %d Correct, %d Incorrect, Score: %d%%", totalCorrectAnswers, totalIncorrectAnswers, score));
    }

    private void displayNextQuestion() {
        if (currentQuestionIndex < questions.size()) {
            currentQuestion = questions.get(currentQuestionIndex);
            updateQuestionDisplay();
            resetButtonStyles();
        } else {
            handleQuizCompletion();
            disableButtons();
            quizScreen_ViewResultsBtn.setDisable(false);
        }
    }

    private void disableButtons() {
        option1.setDisable(true);
        option2.setDisable(true);
        option3.setDisable(true);
        option4.setDisable(true);
    }

    @FXML
    private void handleShowPerformanceReview(ActionEvent event) {
        showPerformanceReview(loggedInUsername);
    }

    public void showPerformanceReview(String UserName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PerformanceReview.fxml"));
            Parent root = loader.load();

            PerformanceReviewController controller = loader.getController();
            controller.init(UserName);

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
