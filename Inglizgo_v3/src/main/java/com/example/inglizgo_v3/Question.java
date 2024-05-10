package com.example.inglizgo_v3;
import java.util.Calendar;
import java.util.Date;

public class Question {
    private int questionId;
    private String questionText;
    private String correctAnswer;
    private int currentRepetition;
    private Date nextReviewDate;
    private int masteryLevel;

    // Constructor
    public Question(int questionId, String questionText, String correctAnswer, int currentRepetition, Date nextReviewDate, int masteryLevel) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.currentRepetition = currentRepetition;
        this.nextReviewDate = nextReviewDate;
        this.masteryLevel = masteryLevel;
    }



    // Getters and Setters
    public int getQuestionId() {
        return questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public int getCurrentRepetition() {
        return currentRepetition;
    }

    public void setCurrentRepetition(int currentRepetition) {
        this.currentRepetition = currentRepetition;
    }

    public Date getNextReviewDate() {
        return nextReviewDate;
    }

    public void setNextReviewDate(Date nextReviewDate) {
        this.nextReviewDate = nextReviewDate;
    }

    public int getMasteryLevel() {
        return masteryLevel;
    }

    public void setMasteryLevel(int masteryLevel) {
        this.masteryLevel = masteryLevel;
    }


    // Update the question progress based on whether the answer was correct
    public void updateProgress(boolean isCorrect) {
        if (isCorrect) {
            if (++currentRepetition >= 6) {
                masteryLevel++;
                currentRepetition = 0;
                // Update next review date based on mastery level
                updateReviewDate();
            }
        } else {
            currentRepetition = 0; // Reset repetition since the answer was wrong
        }
    }



    public void updateReviewDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nextReviewDate); // Set the current next review date

        switch (masteryLevel) {
            case 1: // 1 day later
                calendar.add(Calendar.DATE, 1);
                break;
            case 2: // 1 week later
                calendar.add(Calendar.DATE, 7);
                break;
            case 3: // 1 month later
                calendar.add(Calendar.MONTH, 1);
                break;
            case 4: // 3 months later
                calendar.add(Calendar.MONTH, 3);
                break;
            case 5: // 6 months later
                calendar.add(Calendar.MONTH, 6);
                break;
            case 6: // 1 year later
                calendar.add(Calendar.YEAR, 1);
                break;
            default:
                // This would likely be an error or initialization state
                // Perhaps default to a short interval to ensure visibility in the quiz
                calendar.add(Calendar.DATE, 1);
                break;
        }

        nextReviewDate = calendar.getTime(); // Update the nextReviewDate
    }


}