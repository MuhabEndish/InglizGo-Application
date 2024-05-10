package com.example.inglizgo_v3;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizManager {

    private Connection connect;
    private int loggedInUserId; // User ID to identify the correct user's data

    public QuizManager(int userId) {
        this.loggedInUserId = userId;
        this.connect = connectDB(); // Initialize database connection
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

    public List<Question> fetchQuestionsForReview() {
        List<Question> questions = new ArrayList<>();
        try (Connection conn = connectDB()) {
            String query = "SELECT * FROM questions WHERE nextReviewDate <= CURRENT_DATE() AND masteryLevel < 6 AND userId = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, loggedInUserId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int questionId = rs.getInt("id");
                String questionText = rs.getString("questionText");
                String correctAnswer = rs.getString("correctAnswer");
                int currentRepetition = rs.getInt("currentRepetition");
                Date nextReviewDate = rs.getDate("nextReviewDate");
                int masteryLevel = rs.getInt("masteryLevel");

                Question question = new Question(questionId, questionText, correctAnswer, currentRepetition, nextReviewDate, masteryLevel);
                questions.add(question);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }

    public List<Question> loadQuizData() {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE userId = ? AND NextReviewDate <= CURDATE() AND MasteryLevel < 6";

        try (PreparedStatement stmt = connect.prepareStatement(sql)) {
            stmt.setInt(1, loggedInUserId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Question question = new Question(
                            rs.getInt("id"),
                            rs.getString("questionText"),
                            rs.getString("correctAnswer"),
                            rs.getInt("currentRepetition"),
                            rs.getDate("nextReviewDate"),
                            rs.getInt("masteryLevel")
                    );
                    questions.add(question);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }

    public void submitAnswer(Question question, boolean isCorrect) {
        if (isCorrect) {
            question.setCurrentRepetition(question.getCurrentRepetition() + 1);
            if (question.getCurrentRepetition() >= 6) {
                question.setMasteryLevel(question.getMasteryLevel() + 1);
                question.setCurrentRepetition(0); // Reset repetition count for the new level
            }
        } else {
            question.setCurrentRepetition(0); // Reset because the answer was incorrect
        }
        question.updateReviewDate(); // Update the next review date based on the new mastery level
        updateQuestionInDatabase(question); // Persist changes to the database
    }

    private void updateQuestionInDatabase(Question question) {
        try (Connection conn = connectDB()) {
            String query = "UPDATE questions SET currentRepetition = ?, masteryLevel = ?, nextReviewDate = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, question.getCurrentRepetition());
            pstmt.setInt(2, question.getMasteryLevel());
            pstmt.setDate(3, new Date(question.getNextReviewDate().getTime()));
            pstmt.setInt(4, question.getQuestionId()); // meshaan wxa ka badeshey uso noqo
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to save user answers to the database
    public void saveUserAnswer(UserAnswer userAnswer) {
        String sql = "INSERT INTO user_answers (userId, questionId, lastAnswerCorrect, consecutiveCorrectAnswers, nextDueDate) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connectDB(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userAnswer.getUserId());
            pstmt.setInt(2, userAnswer.getQuestionId());
            pstmt.setBoolean(3, userAnswer.isLastAnswerCorrect());
            pstmt.setInt(4, userAnswer.getConsecutiveCorrectAnswers());
            pstmt.setDate(5, Date.valueOf(userAnswer.getNextDueDate().toString())); // Convert LocalDate to sql.Date
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
