package com.example.inglizgo_v3;

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
    private String UserName;

    public void init(String loggedInUsername) {
        this.UserName = loggedInUsername;
        this.quizManager = new QuizManager(UserName);
        setupTableColumns();
        loadData();
    }

    private void setupTableColumns() {
        colWordId.setCellValueFactory(new PropertyValueFactory<>("wordId"));
        colEnWord.setCellValueFactory(new PropertyValueFactory<>("EN_word"));
        colCorrect.setCellValueFactory(new PropertyValueFactory<>("correctAnswers"));
        colIncorrect.setCellValueFactory(new PropertyValueFactory<>("incorrectAnswers"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalAttempts"));
        colNextReview.setCellValueFactory(new PropertyValueFactory<>("nextReviewDate"));
    }

    private void loadData() {
        try {
            ObservableList<PerformanceData> data = quizManager.getPerformanceData(UserName);
            System.out.println("Data fetched: " + data.size() + " records");
            performanceTable.setItems(data);
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
