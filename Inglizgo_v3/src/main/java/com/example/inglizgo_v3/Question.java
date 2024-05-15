package com.example.inglizgo_v3;

import java.util.List;

public class Question {
    private String enWord;
    private String trTranslate;
    private String firstEx;
    private String secondEx;
    private String correctAnswer;
    private List<String> options;

    public Question(String enWord, String trTranslate, String firstEx, String secondEx, String correctAnswer, List<String> options) {
        this.enWord = enWord;
        this.trTranslate = trTranslate;
        this.firstEx = firstEx;
        this.secondEx = secondEx;
        this.correctAnswer = correctAnswer;
        this.options = options;
    }

    public String getQuestion() {
        return enWord;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public List<String> getShuffledOptions() {
        return options;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getEnWord() {
        return enWord;
    }

    public void setEnWord(String enWord) {
        this.enWord = enWord;
    }

    public String getFirstEx() {
        return firstEx;
    }

    public void setFirstEx(String firstEx) {
        this.firstEx = firstEx;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getSecondEx() {
        return secondEx;
    }

    public void setSecondEx(String secondEx) {
        this.secondEx = secondEx;
    }

    public String getTrTranslate() {
        return trTranslate;
    }

    public void setTrTranslate(String trTranslate) {
        this.trTranslate = trTranslate;
    }
}
