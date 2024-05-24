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

    // Constructor to initialize QuizManager with the logged-in username
    public QuizManager(String UserName) {
        this.loggedInUsername = UserName;
    }

    // Method to establish a connection to the MySQL database
    private Connection connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/inglizgo_app", "root", "");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to insert or update user attempts in the database
    public void insertUserAttempts(List<PerformanceData> performanceDataList) throws SQLException {
        String sql = "INSERT INTO user_attempts (word_id, UserName, correct_answers, total_attempts, attempt_date, next_review_date, repetition) " +
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
                pstmt.setString(2, data.getUserName());
                pstmt.setInt(3, data.getCorrectAnswers());
                pstmt.setInt(4, data.getTotalAttempts());
                pstmt.setTimestamp(5, Timestamp.valueOf(data.getLastAttemptDate()));
                pstmt.setTimestamp(6, Timestamp.valueOf(data.getNextReviewDate()));
                pstmt.setInt(7, data.getRepetition());

                pstmt.addBatch();
            }

            pstmt.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to fetch questions for the user
    public List<Question> fetchQuestionsForUser() {
        List<Question> questions = new ArrayList<>();
        LocalDate today = LocalDate.now();
        String query =  "SELECT wc.word_id, wc.EN_word, wc.TR_translate, wc.FirstEx, wc.SecondEx " +
                "FROM wordcards wc " +
                "LEFT JOIN user_attempts ua ON wc.word_id = ua.word_id AND ua.UserName = ? " +
                "WHERE (ua.next_review_date IS NULL OR DATE(ua.next_review_date) <= ? OR ua.UserName IS NULL) " +
                "AND (wc.UserName = ? OR ua.UserName = ?)";

        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, loggedInUsername);
            pstmt.setDate(2, Date.valueOf(today));
            pstmt.setString(3, loggedInUsername);
            pstmt.setString(4, loggedInUsername);

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
        }
        return questions;
    }

    // Method to get choices for a word excluding the correct answer
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

    // Method to calculate the next review date based on repetition count
    public LocalDateTime getNextReviewDate(int repetition) {
        LocalDateTime now = LocalDateTime.now();
        switch (repetition) {
            case 1:
                return now.plus(1, ChronoUnit.DAYS);
            case 2:
                return now.plus(1, ChronoUnit.WEEKS);
            case 3:
                return now.plus(1, ChronoUnit.MONTHS);
            case 4:
                return now.plus(3, ChronoUnit.MONTHS);
            case 5:
                return now.plus(6, ChronoUnit.MONTHS);
            case 6:
                return now.plus(1, ChronoUnit.YEARS);
            default:
                return now;
        }
    }

    // Method to handle user's answer and update the database accordingly
    public void handleUserAnswer(String UserName, int wordId, boolean isCorrect) {
        int repetition = getCurrentRepetition(UserName, wordId);
        LocalDateTime nextReview;

        if (isCorrect) {
            repetition++;
            nextReview = getNextReviewDate(repetition);
            updateAttempt(UserName, wordId, true, repetition, nextReview);

            if (repetition > 6) {
                moveWordToKnownPool(wordId);
            }
        } else {
            repetition = 1; // reset repetition on incorrect answer
            nextReview = LocalDateTime.now().plusDays(1);
            updateAttempt(UserName, wordId, false, repetition, nextReview);
        }
    }

    // Method to update user's attempt in the database
    public void updateAttempt(String UserName, int wordId, boolean isCorrect, int repetition, LocalDateTime nextReview) {
        String sql;
        if (isCorrect) {
            sql = "UPDATE user_attempts SET correct_answers = 1, repetition = ?," +
                    " next_review_date = ? WHERE UserName = ? AND word_id = ?";
        } else {
            sql = "UPDATE user_attempts SET correct_answers = 0, repetition = 1," +
                    " next_review_date = ? WHERE UserName = ? AND word_id = ?";
        }

        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, repetition);
            pstmt.setTimestamp(2, Timestamp.valueOf(nextReview));
            pstmt.setString(3, UserName);
            pstmt.setInt(4, wordId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to get the current repetition count for a word
    private int getCurrentRepetition(String UserName, int wordId) {
        String sql = "SELECT repetition FROM user_attempts WHERE UserName = ? AND word_id = ?";
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, UserName);
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

    // Method to move a word to the known pool in the database
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

    // Method to reset the progress of a word
    public void resetWordProgress(int wordId) {
        String sql = "UPDATE user_attempts SET repetition = 1, next_review_date = ? WHERE word_id = ?";
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            LocalDateTime nextReviewDate = getNextReviewDate(1);
            pstmt.setTimestamp(1, Timestamp.valueOf(nextReviewDate));
            pstmt.setInt(2, wordId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to get performance data for the user
    public ObservableList<PerformanceData> getPerformanceData(String UserName) {
        ObservableList<PerformanceData> performanceData = FXCollections.observableArrayList();
        String query = "SELECT ua.word_id, wc.EN_word, SUM(ua.correct_answers) AS correctAnswers, " +
                "COUNT(*) AS totalAttempts, COUNT(*) - SUM(ua.correct_answers) AS incorrectAnswers, " +
                "MAX(ua.attempt_date) AS lastAttemptDate, ua.next_review_date, ua.repetition " +
                "FROM user_attempts ua " +
                "JOIN wordcards wc ON ua.word_id = wc.word_id " +
                "WHERE ua.UserName = ? " +
                "GROUP BY ua.word_id, wc.EN_word";
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, UserName);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                return performanceData;
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
                        UserName,
                        wordId,
                        EN_word,
                        correctAnswers,
                        incorrectAnswers,
                        lastAttemptDate,
                        repetition,
                        nextReviewDate,
                        totalAttempts
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return performanceData;
    }

    // Method to print the performance report
    public void printPerformanceReport(ObservableList<PerformanceData> data) {
        try {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(null)) {
                job.getJobSettings().setJobName("Performance Report");

                int itemsPerPage = 3;
                int pageIndex = 0;

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

    // Method to create the content for the performance report
    private Node createContent(ObservableList<PerformanceData> data, int pageIndex, int itemsPerPage) {
        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);

        Label title = new Label(loggedInUsername + "'s Performance Report");
        title.setStyle("-fx-font-size: 23px;");
        content.getChildren().add(title);

        int start = pageIndex;
        int end = Math.min(start + itemsPerPage, data.size());
        for (int i = start; i < end; i++) {
            PerformanceData pd = data.get(i);

            Label wordLabel = new Label("Word ID: " + pd.getWordId() + "\t\tEnglish Word: " + pd.getEN_word());
            Label correctLabel = new Label("Correct Answers: " + pd.getCorrectAnswers());
            Label incorrectLabel = new Label("Incorrect Answers: " + pd.getIncorrectAnswers());
            Label totalAttemptsLabel = new Label("Total Attempts: " + pd.getTotalAttempts());
            Label lastAttemptDateLabel = new Label("Last Attempt Date: " + pd.getLastAttemptDate());
            Label repetitionLabel = new Label("Repetition: " + pd.getRepetition());
            Label nextReviewDateLabel = new Label("Next Review Date: " + pd.getNextReviewDate());

            VBox dataBox = new VBox(5, wordLabel, correctLabel, incorrectLabel, totalAttemptsLabel,
                    lastAttemptDateLabel, repetitionLabel, nextReviewDateLabel);
            dataBox.setStyle("-fx-border-color: #000000; -fx-border-width: 0 0 1px 0; -fx-padding: 10px;");

            content.getChildren().add(dataBox);
        }

        return content;
    }
}
