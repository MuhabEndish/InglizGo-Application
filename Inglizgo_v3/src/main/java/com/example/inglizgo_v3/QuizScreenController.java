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

    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
        this.quizManager = new QuizManager(loggedInUsername);
        loadQuestions();
    }

    public void loadQuestions() {
        questions = quizManager.fetchQuestionsForUser();

        // Check if questions list is empty
        if (questions.isEmpty()) {
            questionLabel.setStyle(" -fx-wrap-text:true; -fx-alignment:center; -fx-font-size:22px");
            questionLabel.setText("There are no questions for today. Try to add new words.");
            disableButtons(); // Optionally disable answer buttons
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
        int userId = quizManager.getUserIdFromUsername(loggedInUsername);
        int wordId = currentQuestion.getWordId();
        LocalDateTime lastAttemptDate = LocalDateTime.now();

        PerformanceData data = performanceDataMap.getOrDefault(wordId, new PerformanceData(
                userId, wordId, currentQuestion.getEnWord(), 0, 0, lastAttemptDate, 0, LocalDateTime.now(), 0));

        boolean shouldReset = !isCorrect || data.getNextReviewDate().isBefore(LocalDateTime.now());

        if (shouldReset) {
            // Reset the values if the answer is wrong or if the next review date is past
            correctAnswersInARow = 0;
            totalIncorrectAnswers++;
            int repetition = 1;  // Reset repetition on incorrect answer or past review date
            LocalDateTime nextReviewDate = LocalDateTime.now().plusDays(1);
            data.setIncorrectAnswers(data.getIncorrectAnswers() + 1);
            data.setRepetition(repetition);
            data.setNextReviewDate(nextReviewDate);
            quizManager.handleUserAnswer(userId, wordId, false);
            selectedButton.setStyle("-fx-background-color: red;  -fx-background-radius: 20px");
            quizManager.updateAttempt(userId, wordId, false, repetition, nextReviewDate);
        } else {
            // Correct answer and review date is not past
            correctAnswersInARow++;
            totalCorrectAnswers++;
            int repetition = data.getRepetition() + 1;
            LocalDateTime nextReviewDate = quizManager.getNextReviewDate(repetition);
            data.setCorrectAnswers(data.getCorrectAnswers() + 1);
            data.setRepetition(repetition);
            data.setNextReviewDate(nextReviewDate);
            quizManager.handleUserAnswer(userId, wordId, true);
            selectedButton.setStyle("-fx-background-color: green; -fx-background-radius: 20px");
            if (repetition > 6) {
                quizManager.moveWordToKnownPool(wordId);
            } else {
                quizManager.updateAttempt(userId, wordId, true, repetition, nextReviewDate);
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
            // or use a logging framework
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
            quizScreen_ViewResultsBtn.setDisable(false); // Enable the button when quiz ends
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
        showPerformanceReview(quizManager.getUserIdFromUsername(loggedInUsername));
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
