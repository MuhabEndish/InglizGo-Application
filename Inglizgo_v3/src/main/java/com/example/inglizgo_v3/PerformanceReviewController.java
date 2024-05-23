package com.example.inglizgo_v3;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class PerformanceReviewController {

    @FXML
    private TableView<PerformanceData> performanceTable; // TableView to display performance data
    @FXML
    private TableColumn<PerformanceData, Integer> colWordId; // Column for word ID
    @FXML
    private TableColumn<PerformanceData, String> colEnWord; // Column for English word
    @FXML
    private TableColumn<PerformanceData, Integer> colCorrect; // Column for correct answers
    @FXML
    private TableColumn<PerformanceData, Integer> colIncorrect; // Column for incorrect answers
    @FXML
    private TableColumn<PerformanceData, Integer> colTotal; // Column for total attempts
    @FXML
    private TableColumn<PerformanceData, String> colNextReview; // Column for next review date

    private QuizManager quizManager; // Instance of QuizManager
    private String UserName; // Username of the logged-in user

    // Method to initialize the controller with the logged-in username
    public void init(String loggedInUsername) {
        this.UserName = loggedInUsername;
        this.quizManager = new QuizManager(UserName);
        setupTableColumns();
        loadData();
    }

    // Method to set up the table columns
    private void setupTableColumns() {
        colWordId.setCellValueFactory(new PropertyValueFactory<>("wordId"));
        colEnWord.setCellValueFactory(new PropertyValueFactory<>("EN_word"));
        colCorrect.setCellValueFactory(new PropertyValueFactory<>("correctAnswers"));
        colIncorrect.setCellValueFactory(new PropertyValueFactory<>("incorrectAnswers"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalAttempts"));
        colNextReview.setCellValueFactory(new PropertyValueFactory<>("nextReviewDate"));
    }

    // Method to load data into the table
    private void loadData() {
        try {
            ObservableList<PerformanceData> data = quizManager.getPerformanceData(UserName);
            System.out.println("Data fetched: " + data.size() + " records");
            performanceTable.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to handle the refresh button click
    @FXML
    private void handleRefresh() {
        loadData();
    }

    // Method to handle the print report button click
    @FXML
    private void handlePrintReport() {
        quizManager.printPerformanceReport(performanceTable.getItems());
    }
}
