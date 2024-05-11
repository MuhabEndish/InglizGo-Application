package com.example.inglizgo_v3;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizManager {
    private int loggedInUserId;
    private Connection connect;

    public QuizManager(int userId) {
        this.loggedInUserId = userId;
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
    public void closeConnection() {
        if (this.connect != null) {
            try {
                this.connect.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public List<Question> fetchQuestionsForReview() {
        List<Question> questions = new ArrayList<>();
        String query = "SELECT * FROM questions WHERE nextReviewDate <= CURDATE() AND masteryLevel < 6 AND userId = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(query)) {
            pstmt.setInt(1, loggedInUserId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                questions.add(extractQuestionFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }

    private Question extractQuestionFromResultSet(ResultSet rs) throws SQLException {
        return new Question(
                rs.getInt("questionId"),
                rs.getString("questionText"),
                rs.getString("correctAnswer"),
                rs.getInt("currentRepetition"),
                rs.getDate("nextReviewDate"),
                rs.getInt("masteryLevel")
        );
    }


    public void submitAnswer(Question question, boolean isCorrect) {
        if (isCorrect) {
            question.setCurrentRepetition(question.getCurrentRepetition() + 1);
            if (question.getCurrentRepetition() >= 6) {
                question.setMasteryLevel(question.getMasteryLevel() + 1);
                question.setCurrentRepetition(0);
            }
        } else {
            question.setCurrentRepetition(0);
        }
        question.updateReviewDate();
        updateQuestionInDatabase(question);
    }

    private void updateQuestionInDatabase(Question question) {
        String sql = "UPDATE questions SET currentRepetition = ?, masteryLevel = ?, nextReviewDate = ? WHERE questionId = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
            pstmt.setInt(1, question.getCurrentRepetition());
            pstmt.setInt(2, question.getMasteryLevel());
            pstmt.setDate(3, new java.sql.Date(question.getNextReviewDate().getTime()));
            pstmt.setInt(4, question.getQuestionId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
