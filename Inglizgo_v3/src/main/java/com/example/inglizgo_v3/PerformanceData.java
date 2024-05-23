package com.example.inglizgo_v3;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PerformanceData {
    private String UserName; // Username associated with the performance data
    private int wordId; // ID of the word
    private String EN_word; // English word
    private int correctAnswers; // Number of correct answers
    private int incorrectAnswers; // Number of incorrect answers
    private LocalDateTime lastAttemptDate; // Date and time of the last attempt
    private int repetition; // Repetition count
    private LocalDateTime nextReviewDate; // Date and time of the next review
    private int totalAttempts; // Total number of attempts

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Formatter for date

    // Constructor to initialize the performance data
    public PerformanceData(String UserName, int wordId, String EN_word, int correctAnswers, int incorrectAnswers, LocalDateTime lastAttemptDate, int repetition, LocalDateTime nextReviewDate, int totalAttempts) {
        this.UserName = UserName;
        this.wordId = wordId;
        this.EN_word = EN_word;
        this.correctAnswers = correctAnswers;
        this.incorrectAnswers = incorrectAnswers;
        this.lastAttemptDate = lastAttemptDate;
        this.repetition = repetition;
        this.nextReviewDate = nextReviewDate;
        this.totalAttempts = totalAttempts;
    }

    // Getters
    public String getUserName() {
        return UserName;
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
        return incorrectAnswers;
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

    // Setters
    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public void setIncorrectAnswers(int incorrectAnswers) {
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
