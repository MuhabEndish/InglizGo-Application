package com.example.inglizgo_v3;

import java.sql.*;
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
        String query = "SELECT * FROM wordcards WHERE UserName = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(query)) {
            pstmt.setString(1, loggedInUsername);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String enWord = rs.getString("EN_word");
                    String trTranslate = rs.getString("TR_translate");
                    String firstEx = rs.getString("FirstEx");
                    String secondEx = rs.getString("SecondEx");

                    List<String> choices = getChoicesForWord(trTranslate);
                    Collections.shuffle(choices); // Seçenekleri karıştır

                    questions.add(new Question(enWord, trTranslate, firstEx, secondEx, trTranslate, choices));
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
            choices.add("BOS");
        }

        return choices;
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
