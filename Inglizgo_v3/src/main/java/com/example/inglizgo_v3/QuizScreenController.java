package com.example.inglizgo_v3;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

import java.util.List;

public class QuizScreenController {

    @FXML
    private Label questionLabel;

    private List<Question> questions; // Assume this is populated somehow
    private int currentQuestionIndex = 0;

    @FXML
    private void initialize() {
        loadNextQuestion();
    }

    private void loadNextQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question question = questions.get(currentQuestionIndex++);
            questionLabel.setText(question.getQuestionText());
        } else {
            questionLabel.setText("No more questions.");
            // Optionally disable Next Question button
        }
    }

    @FXML
    private void handleShowAnswer() {
        Question currentQuestion = questions.get(currentQuestionIndex - 1);
        questionLabel.setText(currentQuestion.getCorrectAnswer());
    }

    @FXML
    private void handleNextQuestion() {
        loadNextQuestion();
    }
}
