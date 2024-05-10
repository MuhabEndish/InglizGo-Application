package com.example.inglizgo_v3;

import java.time.LocalDate;

public class UserAnswer {
    private int userId;
    private int questionId;
    private boolean lastAnswerCorrect;
    private int consecutiveCorrectAnswers;
    private LocalDate nextDueDate;

    public UserAnswer(int userId, int questionId, boolean lastAnswerCorrect, int consecutiveCorrectAnswers, LocalDate nextDueDate) {
        this.userId = userId;
        this.questionId = questionId;
        this.lastAnswerCorrect = lastAnswerCorrect;
        this.consecutiveCorrectAnswers = consecutiveCorrectAnswers;
        this.nextDueDate = nextDueDate;
    }

    // Getters and setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public boolean isLastAnswerCorrect() {
        return lastAnswerCorrect;
    }

    public void setLastAnswerCorrect(boolean lastAnswerCorrect) {
        this.lastAnswerCorrect = lastAnswerCorrect;
    }

    public int getConsecutiveCorrectAnswers() {
        return consecutiveCorrectAnswers;
    }

    public void setConsecutiveCorrectAnswers(int consecutiveCorrectAnswers) {
        this.consecutiveCorrectAnswers = consecutiveCorrectAnswers;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }
}