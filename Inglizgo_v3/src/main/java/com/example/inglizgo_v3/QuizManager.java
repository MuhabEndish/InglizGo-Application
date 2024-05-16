package com.example.inglizgo_v3;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizManager {
    private String loggedInUsername;
    private Connection connect;

    public QuizManager(String username) {
        this.loggedInUsername = username;
        this.connect = connectDB();
    }

    private Connection connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:4306/inglizgo", "root", "");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Question> fetchQuestionsForUser() {
        List<Question> questions = new ArrayList<>();
        String query = "SELECT wordId, EN_word, TR_translate, FirstEx, SecondEx FROM wordcards WHERE UserName = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(query)) {
            pstmt.setString(1, loggedInUsername);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int wordId = rs.getInt("wordId");
                    String enWord = rs.getString("EN_word");
                    String trTranslate = rs.getString("TR_translate");
                    String firstEx = rs.getString("FirstEx");
                    String secondEx = rs.getString("SecondEx");

                    List<String> choices = getChoicesForWord(trTranslate);
                    Collections.shuffle(choices); // Shuffle the choices

                    questions.add(new Question(wordId, enWord, trTranslate, firstEx, secondEx, trTranslate, choices));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }
    private List<String> getChoicesForWord(String correctAnswer) {
        List<String> choices = new ArrayList<>();
        choices.add(correctAnswer);

        // Fetch other choices from the database
        try {
            String sql = "SELECT TR_translate FROM wordcards WHERE TR_translate != ? LIMIT 3";
            PreparedStatement pstmt = connect.prepareStatement(sql);
            pstmt.setString(1, correctAnswer);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                choices.add(rs.getString("TR_translate"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // If there are not enough other choices, add dummy choices (for testing purposes)
        while (choices.size() < 4) {
            choices.add("Empty!");
        }

        return choices;
    }

    public LocalDateTime getNextReviewDate(int repetition) {
        LocalDateTime now = LocalDateTime.now();
        switch (repetition) {
            case 1: return now.plus(1, ChronoUnit.DAYS);
            case 2: return now.plus(1, ChronoUnit.WEEKS);
            case 3: return now.plus(1, ChronoUnit.MONTHS);
            case 4: return now.plus(3, ChronoUnit.MONTHS);
            case 5: return now.plus(6, ChronoUnit.MONTHS);
            case 6: return now.plus(1, ChronoUnit.YEARS);
            default: return now;
        }
    }
    public void handleUserAnswer(int userId, int wordId, boolean isCorrect) {
        int repetition = getCurrentRepetition(userId, wordId);
        if (isCorrect) {
            repetition++;
            if (repetition > 6) {
                moveWordToKnownPool(wordId);
            } else {
                LocalDateTime nextReview = getNextReviewDate(repetition);
                updateAttempt(userId, wordId, true, repetition, nextReview);
            }
        } else {
            LocalDateTime nextReview = getNextReviewDate(1); // Reset to first interval
            updateAttempt(userId, wordId, false, 1, nextReview);
        }
    }
    private void updateAttempt(int userId, int wordId, boolean isCorrect, int repetition, LocalDateTime nextReview) {
        String sql = "UPDATE user_attempts SET correct = ?, repetition = ?, next_review_date = ? WHERE user_id = ? AND word_id = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
            pstmt.setBoolean(1, isCorrect);
            pstmt.setInt(2, repetition);
            pstmt.setTimestamp(3, Timestamp.valueOf(nextReview));
            pstmt.setInt(4, userId);
            pstmt.setInt(5, wordId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private int getCurrentRepetition(int userId, int wordId) {
        String sql = "SELECT current_repetition FROM user_attempts WHERE userId = ? AND wordId = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, wordId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("current_repetition");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;  // Return 0 if no record found, or in case of an error
    }

    public void moveWordToKnownPool(int wordId) {
        String sql = "UPDATE wordcards SET status = 'known' WHERE wordId = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
            pstmt.setInt(1, wordId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void resetWordProgress(int wordId) {
        // Example SQL to reset the progress of a word
        String sql = "UPDATE user_attempts SET repetition = 1, next_review_date = ? WHERE word_id = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(getNextReviewDate(1)));
            pstmt.setInt(2, wordId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        if (this.connect != null) {
            try {
                this.connect.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
