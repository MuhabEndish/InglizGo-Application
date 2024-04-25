package com.example.inglizgo_v3;


public class MyWordCard {

    private String EN_word;
    private String TR_translate;
    private String FirstEx;
    private String SecondEx;

    // Constructor
    public MyWordCard(String EN_word, String TR_translate, String FirstEx, String SecondEx) {
        this.EN_word = EN_word;
        this.TR_translate = TR_translate;
        this.FirstEx = FirstEx;
        this.SecondEx = SecondEx;
    }

    // Getters and setters
    public String getEN_word() {
        return EN_word;
    }

    public void setEN_word(String EN_word) {
        this.EN_word = EN_word;
    }

    public String getTR_translate() {
        return TR_translate;
    }

    public void setTR_translate(String TR_translate) {
        this.TR_translate = TR_translate;
    }

    public String getFirstEx() {
        return FirstEx;
    }

    public void setFirstEx(String FirstEx) {
        this.FirstEx = FirstEx;
    }

    public String getSecondEx() {
        return SecondEx;
    }

    public void setSecondEx(String SecondEx) {
        this.SecondEx = SecondEx;
    }

    // Override toString() method for easy printing
    @Override
    public String toString() {
        return "WordCard{" +
                "EN_word='" + EN_word + '\'' +
                ", TR_translate='" + TR_translate + '\'' +
                ", FirstEx='" + FirstEx + '\'' +
                ", SecondEx='" + SecondEx + '\'' +
                '}';
    }
}
