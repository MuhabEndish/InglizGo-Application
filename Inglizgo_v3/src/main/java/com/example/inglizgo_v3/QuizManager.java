package com.example.inglizgo_v3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizManager {

    private String loggedInUsername;

    public QuizManager(String username) {
        this.loggedInUsername = username;
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

    public int getUserIdFromUsername(String username) {
        int userId = -1;  // Default value if user is not found
        String query = "SELECT user_id FROM user_info WHERE UserName = ?";
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    userId = rs.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userId;
    }

    public List<Question> fetchQuestionsForUser() {
        String query = "SELECT word_id, EN_word, TR_translate, FirstEx, SecondEx FROM wordcards WHERE UserName = ?";
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, loggedInUsername);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.println("No questions found for user: " + loggedInUsername);
                    return new ArrayList<>();
                }
                List<Question> questions = new ArrayList<>();
                while (rs.next()) {
                    List<String> options = getChoicesForWord(rs.getString("TR_translate"));
                    questions.add(new Question(
                            rs.getInt("word_id"),
                            rs.getString("EN_word"),
                            rs.getString("TR_translate"),
                            rs.getString("FirstEx"),
                            rs.getString("SecondEx"),
                            rs.getString("TR_translate"), // Assuming 'TR_translate' is the correct answer
                            options
                    ));
                }
                return questions;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL error occurred while fetching questions for user.");
            return new ArrayList<>();
        }
    }

    private List<String> getChoicesForWord(String correctAnswer) {
        List<String> choices = new ArrayList<>();
        choices.add(correctAnswer);
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement("SELECT TR_translate FROM wordcards WHERE TR_translate != ? LIMIT 3")) {
            pstmt.setString(1, correctAnswer);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                choices.add(rs.getString("TR_translate"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        while (choices.size() < 4) {
            choices.add("Empty!");
        }
        Collections.shuffle(choices);
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
        LocalDateTime nextReview;
        if (isCorrect) {
            repetition++;
            nextReview = getNextReviewDate(repetition);
            if (repetition > 6) {
                moveWordToKnownPool(wordId);
            } else {
                updateAttempt(userId, wordId, true, repetition, nextReview);
            }
        } else {
            repetition = 1; // reset repetition on incorrect answer
            nextReview = LocalDateTime.now().plusDays(1);
            updateAttempt(userId, wordId, false, repetition, nextReview);
        }
    }

    public void updateAttempt(int userId, int wordId, boolean isCorrect, int repetition, LocalDateTime nextReview) {
        String sql = "UPDATE user_attempts SET correct = ?, repetition = ?, next_review_date = ? WHERE user_id = ? AND word_id = ?";
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
        String sql = "SELECT repetition FROM user_attempts WHERE user_id = ? AND word_id = ?";
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, wordId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("repetition");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void moveWordToKnownPool(int wordId) {
        String sql = "UPDATE wordcards SET status = 'known' WHERE word_id = ?";
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, wordId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void resetWordProgress(int wordId) {
        String sql = "UPDATE user_attempts SET repetition = 1, next_review_date = ? WHERE word_id = ?";
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            LocalDateTime nextReviewDate = getNextReviewDate(1);  // Gets the next review date after one day.
            pstmt.setTimestamp(1, Timestamp.valueOf(nextReviewDate));  // Convert LocalDateTime to Timestamp.
            pstmt.setInt(2, wordId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<PerformanceData> getPerformanceData(int userId) {
        ObservableList<PerformanceData> performanceData = FXCollections.observableArrayList();
        String query = "SELECT word_id, SUM(correct) AS correctAnswers, COUNT(*) AS totalAttempts, COUNT(*) - SUM(correct) AS incorrectAnswers, MAX(attempt_date) AS lastAttemptDate, next_review_date, repetition " +
                "FROM user_attempts WHERE user_id = ? GROUP BY word_id";
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("No data fetched for user ID: " + userId);
                return performanceData; // return empty list early if no data
            }
            while (rs.next()) {
                int wordId = rs.getInt("word_id");
                int correctAnswers = rs.getInt("correctAnswers");
                int incorrectAnswers = rs.getInt("incorrectAnswers");
                int totalAttempts = rs.getInt("totalAttempts");
                String lastAttemptDate = rs.getString("lastAttemptDate");
                String nextReviewDate = rs.getString("next_review_date");
                int repetition = rs.getInt("repetition");

                performanceData.add(new PerformanceData(
                        userId,
                        wordId,
                        correctAnswers,
                        lastAttemptDate,
                        repetition,
                        nextReviewDate,
                        totalAttempts
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL error occurred while fetching performance data: " + e.getMessage());
        }
        return performanceData;
    }



    public void printPerformanceReport(ObservableList<PerformanceData> data) {
        try {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(null)) {
                job.getJobSettings().setJobName("Performance Report");
                Node reportContent = createContent(data);
                boolean success = job.printPage(reportContent);
                if (success) {
                    job.endJob();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Node createContent(ObservableList<PerformanceData> data) {
        VBox content = new VBox(10);
        for (PerformanceData pd : data) {
            Label label = new Label("User: " + pd.getUserId() + ", Word ID: " + pd.getWordId() + ", Correct: " + pd.getCorrect());
            content.getChildren().add(label);
        }
        return content;
    }
}
