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

    // Constructor to initialize the Question object
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

    // Getter for wordId
    public int getWordId() {
        return wordId;
    }

    // Getter for enWord (English word)
    public String getEnWord() {
        return enWord;
    }

    // Getter for trTranslate (Turkish translation)
    public String getTrTranslate() {
        return trTranslate;
    }

    // Getter for firstEx (first example sentence)
    public String getFirstEx() {
        return firstEx;
    }

    // Getter for secondEx (second example sentence)
    public String getSecondEx() {
        return secondEx;
    }

    // Getter for correctAnswer (correct translation)
    public String getCorrectAnswer() {
        return correctAnswer;
    }

    // Getter for options (list of answer options)
    public List<String> getOptions() {
        return options;
    }

    // Getter for correctCount (number of correct answers)
    public int getCorrectCount() {
        return correctCount;
    }

    // Getter for incorrectCount (number of incorrect answers)
    public int getIncorrectCount() {
        return incorrectCount;
    }

    // Getter for lastAttemptDate (date of last attempt)
    public String getLastAttemptDate() {
        return lastAttemptDate;
    }

    // Getter for nextReviewDate (date of next review)
    public String getNextReviewDate() {
        return nextReviewDate;
    }

    // Setter for wordId
    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    // Setter for enWord (checks for null or empty value before setting)
    public void setEnWord(String enWord) {
        if (enWord != null && !enWord.isEmpty()) {
            this.enWord = enWord;
        }
    }

    // Setter for trTranslate (checks for null or empty value before setting)
    public void setTrTranslate(String trTranslate) {
        if (trTranslate != null && !trTranslate.isEmpty()) {
            this.trTranslate = trTranslate;
        }
    }

    // Setter for firstEx
    public void setFirstEx(String firstEx) {
        this.firstEx = firstEx;
    }

    // Setter for secondEx
    public void setSecondEx(String secondEx) {
        this.secondEx = secondEx;
    }

    // Setter for correctAnswer (checks for null or empty value before setting)
    public void setCorrectAnswer(String correctAnswer) {
        if (correctAnswer != null && !correctAnswer.isEmpty()) {
            this.correctAnswer = correctAnswer;
        }
    }

    // Setter for options (checks for null or empty list before setting)
    public void setOptions(List<String> options) {
        if (options != null && !options.isEmpty()) {
            this.options = options;
        }
    }

    // Setter for correctCount (checks for non-negative value before setting)
    public void setCorrectCount(int correctCount) {
        if (correctCount >= 0) {
            this.correctCount = correctCount;
        }
    }

    // Setter for incorrectCount (checks for non-negative value before setting)
    public void setIncorrectCount(int incorrectCount) {
        if (incorrectCount >= 0) {
            this.incorrectCount = incorrectCount;
        }
    }

    // Setter for lastAttemptDate
    public void setLastAttemptDate(String lastAttemptDate) {
        this.lastAttemptDate = lastAttemptDate;
    }

    // Setter for nextReviewDate
    public void setNextReviewDate(String nextReviewDate) {
        this.nextReviewDate = nextReviewDate;
    }

    // Method to shuffle the options list
    public List<String> getShuffledOptions() {
        Collections.shuffle(this.options);
        return this.options;
    }
}
