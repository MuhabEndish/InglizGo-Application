package com.example.inglizgo_v3;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PerformanceData {
    private int userId;
    private int wordId;
    private String EN_word;
    private int correctAnswers;
    private int incorrectAnswers;
    private LocalDateTime lastAttemptDate;
    private int repetition;
    private LocalDateTime nextReviewDate;
    private int totalAttempts;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public PerformanceData(int userId, int wordId, String EN_word, int correctAnswers, int incorrectAnswers, LocalDateTime lastAttemptDate, int repetition, LocalDateTime nextReviewDate, int totalAttempts) {
        this.userId = userId;
        this.wordId = wordId;
        this.EN_word=EN_word;
        this.correctAnswers = correctAnswers;
        this.incorrectAnswers = incorrectAnswers; // Add this parameter
        this.lastAttemptDate = lastAttemptDate;
        this.repetition = repetition;
        this.nextReviewDate = nextReviewDate;
        this.totalAttempts = totalAttempts;
    }

    public int getUserId() {
        return userId;
    }

    public int getWordId() {
        return wordId;
    }

    public String getEN_word() {
        return EN_word;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public int getIncorrectAnswers() {
        return incorrectAnswers; // Add this method
    }

    public LocalDateTime getLastAttemptDate() {
        return lastAttemptDate;
    }

    public int getRepetition() {
        return repetition;
    }

    public LocalDateTime getNextReviewDate() {
        return nextReviewDate;
    }

    public int getTotalAttempts() {
        return totalAttempts;
    }

    public void setEN_word(String EN_word) {
        this.EN_word = EN_word;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public void setIncorrectAnswers(int incorrectAnswers) { // Add this method
        this.incorrectAnswers = incorrectAnswers;
    }


    public void setLastAttemptDate(LocalDateTime lastAttemptDate) {
        this.lastAttemptDate = lastAttemptDate;
    }

    public void setRepetition(int repetition) {
        this.repetition = repetition;
    }

    public void setNextReviewDate(LocalDateTime nextReviewDate) {
        this.nextReviewDate = nextReviewDate;
    }

    public void setTotalAttempts(int totalAttempts) {
        this.totalAttempts = totalAttempts;
    }
}