package com.example.inglizgo_v3;

import com.mysql.cj.callback.UsernameCallback;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.Parent;





public class QuizScreenController {

    @FXML
    private Label questionLabel;

    private List<Question> questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private boolean showingAnswer = false;

//    public void loadQuestions() {
//        QuizManager quizManager = new QuizManager(userId); // Adjust according to how you manage user IDs
//        this.questions = quizManager.fetchQuestionsForReview();
//        if (!questions.isEmpty()) {
//            loadNextQuestion();
//        } else {
//            questionLabel.setText("No questions available for review.");
//        }
//    }


    @FXML
    private void initialize() {
        populateQuestions();
        loadNextQuestion();
    }

    private void populateQuestions() {

    }

    private void loadNextQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question question = questions.get(currentQuestionIndex);
            questionLabel.setText(question.getQuestionText());
            showingAnswer = false;
        } else {
            questionLabel.setText("Tap to reveal.");
        }
    }



    @FXML
    private void handleShowAnswer() {
        if (!questions.isEmpty() && currentQuestionIndex < questions.size()) {
            Question currentQuestion = questions.get(currentQuestionIndex);
            if (showingAnswer) {
                questionLabel.setText(currentQuestion.getQuestionText());
                showingAnswer = false;
            } else {
                questionLabel.setText(currentQuestion.getCorrectAnswer());
                showingAnswer = true;
            }
        }
    }
    @FXML
    private void handleBackToMain() {
        try {
            // Load the main page FXML
            Parent root = FXMLLoader.load(getClass().getResource("mainForm.fxml")); // Ensure this path is correct
            // Get the stage from an existing component in your scene
            Stage stage = (Stage) questionLabel.getScene().getWindow(); // Assuming questionLabel is part of the current scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
