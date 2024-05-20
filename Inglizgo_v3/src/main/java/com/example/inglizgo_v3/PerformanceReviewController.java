package com.example.inglizgo_v3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class PerformanceReviewController {

    @FXML
    private TableView<PerformanceData> performanceTable;
    @FXML
    private TableColumn<PerformanceData, Integer> colWordId;
    @FXML
    private TableColumn<PerformanceData, String> colEnWord;
    @FXML
    private TableColumn<PerformanceData, Integer> colCorrect;
    @FXML
    private TableColumn<PerformanceData, Integer> colIncorrect;
    @FXML
    private TableColumn<PerformanceData, Integer> colTotal;
    @FXML
    private TableColumn<PerformanceData, String> colNextReview;

    private QuizManager quizManager;
    private String username;

    public void init(String loggedInUsername) {
        this.username = loggedInUsername;
        this.quizManager = new QuizManager(username);
        setupTableColumns();
        loadData();
    }
    private void setupTableColumns() {
        colWordId.setCellValueFactory(new PropertyValueFactory<>("wordId"));
        colEnWord.setCellValueFactory(new PropertyValueFactory<>("enWord"));
        colCorrect.setCellValueFactory(new PropertyValueFactory<>("correctAnswers"));
        colIncorrect.setCellValueFactory(new PropertyValueFactory<>("incorrectAnswers"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalAttempts"));
        colNextReview.setCellValueFactory(new PropertyValueFactory<>("nextReviewDate"));
    }

    private void loadData() {
        try {
            int userId = quizManager.getUserIdFromUsername(username);
            if (userId != -1) {
                ObservableList<PerformanceData> data = quizManager.getPerformanceData(userId);
                System.out.println("Data fetched: " + data.size() + " records");
                performanceTable.setItems(data);
            } else {
                System.out.println("User ID not found for username: " + username);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadData();
    }

    @FXML
    private void handlePrintReport() {
        quizManager.printPerformanceReport(performanceTable.getItems());
    }
}
