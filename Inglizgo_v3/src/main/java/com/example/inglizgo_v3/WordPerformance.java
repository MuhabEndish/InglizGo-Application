package com.example.inglizgo_v3;


public class WordPerformance {
    private int wordId;
    private int totalAttempts;
    private int correctAnswers;
    private int incorrectAnswers;


    public WordPerformance(int wordId, int totalAttempts, int correctAnswers, int incorrectAnswers) {
        this.wordId = wordId;
        this.totalAttempts = totalAttempts;
        this.correctAnswers = correctAnswers;
        this.incorrectAnswers = incorrectAnswers;


    }

    // Getter ve setter metotları
    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    public int getTotalAttempts() {
        return totalAttempts;
    }

    public void setTotalAttempts(int totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getIncorrectAnswers() {
        return incorrectAnswers;
    }

    public void setIncorrectAnswers(int incorrectAnswers) {
        this.incorrectAnswers = incorrectAnswers;
    }
}
