package com.example.inglizgo_v3;

public class PerformanceData {
    private int userId;
    private int wordId;
    private String enWord;  // English word
    private int correct;
    private int incorrect;
    private int totalAttempts;
    private String nextReviewDate;
    private String attemptDate;  // Date of attempt

    public PerformanceData(int userId, int wordId, String enWord, int correct, int attemptDate, int totalAttempts, String nextReviewDate) {
        this.userId = userId;
        this.wordId = wordId;
        this.enWord = enWord;
        this.correct = correct;
        this.incorrect = totalAttempts - correct;
        this.totalAttempts = totalAttempts;
        this.attemptDate = String.valueOf(attemptDate);
        this.nextReviewDate = nextReviewDate;
    }

    // Getters
    public int getUserId() { return userId; }
    public int getWordId() { return wordId; }
    public String getEnWord() { return enWord; }
    public int getCorrect() { return correct; }
    public int getIncorrect() { return incorrect; }
    public int getTotalAttempts() { return totalAttempts; }
    public String getNextReviewDate() { return nextReviewDate; }
    public String getAttemptDate() { return attemptDate; }

    // Setters
    public void setUserId(int userId) { this.userId = userId; }
    public void setWordId(int wordId) { this.wordId = wordId; }
    public void setEnWord(String enWord) { this.enWord = enWord; }
    public void setCorrect(int correct) { this.correct = correct; }
    public void setIncorrect(int incorrect) { this.incorrect = incorrect; }
    public void setTotalAttempts(int totalAttempts) { this.totalAttempts = totalAttempts; }
    public void setNextReviewDate(String nextReviewDate) { this.nextReviewDate = nextReviewDate; }
    public void setAttemptDate(String attemptDate) { this.attemptDate = attemptDate; }
}
