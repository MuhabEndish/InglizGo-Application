package com.example.inglizgo_v3;

public class PerformanceData {
    private int userId;
    private int wordId;
    private int correct;
    private int incorrect;
    private int totalAttempts;
    private String nextReviewDate;

    public PerformanceData(int userId, int wordId, int correct, String attemptDate, int repetition, String nextReviewDate, int totalAttempts) {
        this.userId = userId;
        this.wordId = wordId;
        this.correct = correct;
        this.incorrect = totalAttempts - correct;
        this.totalAttempts = totalAttempts;
        this.nextReviewDate = nextReviewDate;
    }

    // Getters
    public int getUserId() { return userId; }
    public int getWordId() { return wordId; }
    public int getCorrect() { return correct; }
    public int getIncorrect() { return incorrect; }
    public int getTotalAttempts() { return totalAttempts; }
    public String getNextReviewDate() { return nextReviewDate; }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public void setIncorrect(int incorrect) {
        this.incorrect = incorrect;
    }

    public void setTotalAttempts(int totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public void setNextReviewDate(String nextReviewDate) {
        this.nextReviewDate = nextReviewDate;
    }
}
