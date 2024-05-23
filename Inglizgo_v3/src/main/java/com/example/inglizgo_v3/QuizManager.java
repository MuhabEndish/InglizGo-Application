package com.example.inglizgo_v3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.sql.*;
import java.time.LocalDate;
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
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/inglizgo_app", "root", "");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void insertUserAttempts(List<PerformanceData> performanceDataList) throws SQLException {
        // SQL query for inserting or updating user attempts
        String sql = "INSERT INTO user_attempts (word_id, user_id, correct_answers, total_attempts, attempt_date, next_review_date, repetition) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "correct_answers = VALUES(correct_answers), " +
                "total_attempts = VALUES(total_attempts), " +
                "attempt_date = VALUES(attempt_date), " +
                "next_review_date = VALUES(next_review_date), " +
                "repetition = VALUES(repetition)";

        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (PerformanceData data : performanceDataList) {
                pstmt.setInt(1, data.getWordId());
                pstmt.setInt(2, data.getUserId());
                pstmt.setInt(3, data.getCorrectAnswers());
                pstmt.setInt(4, data.getTotalAttempts());
                pstmt.setTimestamp(5, Timestamp.valueOf(data.getLastAttemptDate())); // Convert LocalDateTime to Timestamp
                pstmt.setTimestamp(6, Timestamp.valueOf(data.getNextReviewDate())); // Convert LocalDateTime to Timestamp
                pstmt.setInt(7, data.getRepetition());

                pstmt.addBatch();
            }

            pstmt.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
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
        List<Question> questions = new ArrayList<>();
        LocalDate today = LocalDate.now();
        String query = "SELECT wc.word_id, wc.EN_word, wc.TR_translate, wc.FirstEx, wc.SecondEx " +
                "FROM wordcards wc " +
                "LEFT JOIN user_attempts ua ON wc.word_id = ua.word_id AND ua.user_id = ? " +
                "WHERE (ua.next_review_date IS NULL OR ua.next_review_date <= ?)";

        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, getUserIdFromUsername(loggedInUsername));
            pstmt.setDate(2, Date.valueOf(today));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    List<String> options = getChoicesForWord(rs.getString("TR_translate"));
                    questions.add(new Question(
                            rs.getInt("word_id"),
                            rs.getString("EN_word"),
                            rs.getString("TR_translate"),
                            rs.getString("FirstEx"),
                            rs.getString("SecondEx"),
                            rs.getString("TR_translate"),
                            options
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL error occurred while fetching questions for user.");
        }
        return questions;
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
            updateAttempt(userId, wordId, true, repetition, nextReview);

            if (repetition > 6) {
                moveWordToKnownPool(wordId);
            }
        } else {
            repetition = 1; // reset repetition on incorrect answer
            nextReview = LocalDateTime.now().plusDays(1);
            updateAttempt(userId, wordId, false, repetition, nextReview);
        }
    }

    public void updateAttempt(int userId, int wordId, boolean isCorrect, int repetition, LocalDateTime nextReview) {
        String sql;
        if (isCorrect) {
            sql = "UPDATE user_attempts SET correct_answers = 1, repetition = ?," +
                    " next_review_date = ? WHERE user_id = ? AND word_id = ?";
        } else {
            sql = "UPDATE user_attempts SET correct_answers = 0, repetition = 1," +
                    " next_review_date = ? WHERE user_id = ? AND word_id = ?";
        }

        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, repetition);
            pstmt.setTimestamp(2, Timestamp.valueOf(nextReview));
            pstmt.setInt(3, userId);
            pstmt.setInt(4, wordId);
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
        String query = "SELECT ua.word_id, wc.EN_word, SUM(ua.correct_answers) AS correctAnswers, " +
                "COUNT(*) AS totalAttempts, COUNT(*) - SUM(ua.correct_answers) AS incorrectAnswers, " +
                "MAX(ua.attempt_date) AS lastAttemptDate, ua.next_review_date, ua.repetition " +
                "FROM user_attempts ua " +
                "JOIN wordcards wc ON ua.word_id = wc.word_id " +
                "WHERE ua.user_id = ? " +
                "GROUP BY ua.word_id, wc.EN_word";
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
                String EN_word = rs.getString("EN_word");
                int correctAnswers = rs.getInt("correctAnswers");
                int incorrectAnswers = rs.getInt("incorrectAnswers");
                int totalAttempts = rs.getInt("totalAttempts");
                int repetition = rs.getInt("repetition");
                LocalDateTime lastAttemptDate = rs.getTimestamp("lastAttemptDate").toLocalDateTime();
                LocalDateTime nextReviewDate = rs.getTimestamp("next_review_date").toLocalDateTime();

                performanceData.add(new PerformanceData(
                        userId,
                        wordId,
                        EN_word,
                        correctAnswers,
                        incorrectAnswers, // incorrectAnswers should be set appropriately
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

                int itemsPerPage = 3; // Set fixed number of items per page to 3
                int pageIndex = 0;

                // Loop through the data, creating and printing each page
                while (pageIndex < data.size()) {
                    Node reportContent = createContent(data, pageIndex, itemsPerPage);
                    boolean success = job.printPage(reportContent);
                    if (!success) {
                        break;
                    }
                    pageIndex += itemsPerPage;
                }

                job.endJob();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Node createContent(ObservableList<PerformanceData> data, int pageIndex, int itemsPerPage) {
        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);

        // Add title to the content
        Label title = new Label(loggedInUsername +"'s Performance Report");
        title.setStyle("-fx-font-size: 23px;");
        content.getChildren().add(title);

        int start = pageIndex;
        int end = Math.min(start + itemsPerPage, data.size());
        for (int i = start; i < end; i++) {
            PerformanceData pd = data.get(i);

            // Create labels for each item
            Label wordLabel = new Label("Word ID: " + pd.getWordId() + "\t\tEnglish Word: " + pd.getEN_word());
            Label correctLabel = new Label("Correct Answers: " + pd.getCorrectAnswers());
            Label incorrectLabel = new Label("Incorrect Answers: " + pd.getIncorrectAnswers());
            Label totalAttemptsLabel = new Label("Total Attempts: " + pd.getTotalAttempts());
            Label lastAttemptDateLabel = new Label("Last Attempt Date: " + pd.getLastAttemptDate());
            Label repetitionLabel = new Label("Repetition: " + pd.getRepetition());
            Label nextReviewDateLabel = new Label("Next Review Date: " + pd.getNextReviewDate());

            // Group labels into a VBox for each data item
            VBox dataBox = new VBox(5, wordLabel, correctLabel, incorrectLabel, totalAttemptsLabel,
                    lastAttemptDateLabel, repetitionLabel, nextReviewDateLabel);
            dataBox.setStyle("-fx-border-color: #000000; -fx-border-width: 0 0 1px 0; -fx-padding: 10px;");

            content.getChildren().add(dataBox);
        }

        return content;
    }
}