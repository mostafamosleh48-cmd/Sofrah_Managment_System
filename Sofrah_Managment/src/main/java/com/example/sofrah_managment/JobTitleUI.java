package com.example.sofrah_managment;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class JobTitleUI {

    private TableView<JobTitle> jobTitleTable;
    private ObservableList<JobTitle> jobTitleData;

    public Node getView() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Job Titles");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #333333;");

        jobTitleTable = new TableView<>();
        setupTableColumns();
        refreshTable();

        // Buttons
        Button addBtn = new Button("➕ Add Job Title");
        Button refreshBtn = new Button("🔄 Refresh");

        addBtn.setOnAction(e -> handleAddJobTitle());
        refreshBtn.setOnAction(e -> refreshTable());

        HBox buttonBox = new HBox(10, addBtn, refreshBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 10, 0));

        VBox centerContent = new VBox(10, titleLabel, jobTitleTable, buttonBox);
        centerContent.setAlignment(Pos.TOP_CENTER);
        layout.setCenter(centerContent);

        return layout;
    }

    private void setupTableColumns() {
        TableColumn<JobTitle, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<JobTitle, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<JobTitle, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<JobTitle, Double> payRateCol = new TableColumn<>("Pay Rate");
        payRateCol.setCellValueFactory(new PropertyValueFactory<>("payRate"));

        jobTitleTable.getColumns().addAll(idCol, nameCol, descCol, payRateCol);
    }

    private void refreshTable() {
        jobTitleData = FXCollections.observableArrayList(JobTitleManagement.getAllJobTitles());
        jobTitleTable.setItems(jobTitleData);
    }

    private void handleAddJobTitle() {
        Dialog<JobTitle> dialog = new Dialog<>();
        dialog.setTitle("Add Job Title");

        TextField nameField = new TextField();
        TextField descField = new TextField();
        TextField payRateField = new TextField();

        VBox content = new VBox(10,
                new Label("Name:"), nameField,
                new Label("Description:"), descField,
                new Label("Pay Rate:"), payRateField
        );
        content.setPadding(new Insets(15));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    String name = nameField.getText().trim();
                    String desc = descField.getText().trim();
                    double payRate = Double.parseDouble(payRateField.getText().trim());

                    JobTitle jobTitle = new JobTitle(0, name, desc, payRate);
                    boolean success = addJobTitleToDatabase(jobTitle);
                    if (success) {
                        refreshTable();
                        showInfo("Job title added successfully.");
                    } else {
                        showError("Failed to add job title.");
                    }
                } catch (Exception e) {
                    showError("Invalid input: " + e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private boolean addJobTitleToDatabase(JobTitle job) {
        String sql = "INSERT INTO jobtitle (Name, Description, PayRate) VALUES (?, ?, ?)";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, job.getName());
            stmt.setString(2, job.getDescription());
            stmt.setDouble(3, job.getPayRate());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error adding job title: " + e.getMessage());
            return false;
        }
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
