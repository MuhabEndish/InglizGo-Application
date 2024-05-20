package com.example.inglizgo_v3;

import java.util.Collections;
import java.util.List;

public class Question {
    private int wordId;
    private String enWord;
    private String trTranslate;
    private String firstEx;
    private String secondEx;
    private String correctAnswer;
    private List<String> options;
    private int correctCount;
    private int incorrectCount;
    private String lastAttemptDate;
    private String nextReviewDate;

    // Constructor
    public Question(int wordId, String enWord, String trTranslate, String firstEx, String secondEx,
                    String correctAnswer, List<String> options) {
        this.wordId = wordId;
        this.enWord = enWord;
        this.trTranslate = trTranslate;
        this.firstEx = firstEx;
        this.secondEx = secondEx;
        this.correctAnswer = correctAnswer;
        this.options = options;
        this.correctCount = correctCount;
        this.incorrectCount = incorrectCount;
        this.lastAttemptDate = lastAttemptDate;
        this.nextReviewDate = nextReviewDate;
    }

    // Getters
    public int getWordId() {
        return wordId;
    }

    public String getEnWord() {
        return enWord;
    }

    public String getTrTranslate() {
        return trTranslate;
    }

    public String getFirstEx() {
        return firstEx;
    }

    public String getSecondEx() {
        return secondEx;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public int getIncorrectCount() {
        return incorrectCount;
    }

    public String getLastAttemptDate() {
        return lastAttemptDate;
    }

    public String getNextReviewDate() {
        return nextReviewDate;
    }

    // Setters
    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    public void setEnWord(String enWord) {
        if (enWord != null && !enWord.isEmpty()) {
            this.enWord = enWord;
        }
    }

    public void setTrTranslate(String trTranslate) {
        if (trTranslate != null && !trTranslate.isEmpty()) {
            this.trTranslate = trTranslate;
        }
    }

    public void setFirstEx(String firstEx) {
        this.firstEx = firstEx;
    }

    public void setSecondEx(String secondEx) {
        this.secondEx = secondEx;
    }

    public void setCorrectAnswer(String correctAnswer) {
        if (correctAnswer != null && !correctAnswer.isEmpty()) {
            this.correctAnswer = correctAnswer;
        }
    }

    public void setOptions(List<String> options) {
        if (options != null && !options.isEmpty()) {
            this.options = options;
        }
    }

    public void setCorrectCount(int correctCount) {
        if (correctCount >= 0) {
            this.correctCount = correctCount;
        }
    }

    public void setIncorrectCount(int incorrectCount) {
        if (incorrectCount >= 0) {
            this.incorrectCount = incorrectCount;
        }
    }

    public void setLastAttemptDate(String lastAttemptDate) {
        this.lastAttemptDate = lastAttemptDate;
    }

    public void setNextReviewDate(String nextReviewDate) {
        this.nextReviewDate = nextReviewDate;
    }

    public List<String> getShuffledOptions() {
        Collections.shuffle(this.options);
        return this.options;
    }
}
