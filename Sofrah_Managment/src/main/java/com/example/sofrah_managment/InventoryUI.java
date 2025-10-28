package com.example.sofrah_managment;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class InventoryUI {
    private TableView<InventoryItem> table;
    private VBox view;

    public InventoryUI() {
        table = new TableView<>();
        view = new VBox(10);
        view.setPadding(new Insets(20));

        setupTable();
        setupButtons();
        refreshTable();
    }

    private void setupTable() {
        TableColumn<InventoryItem, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<InventoryItem, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<InventoryItem, Integer> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));

        table.getColumns().addAll(idCol, nameCol, stockCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupButtons() {
        Button addBtn = new Button("Add Item");
        Button updateStockBtn = new Button("Update Stock");
        Button lowStockBtn = new Button("Low Stock");
        Button refreshBtn = new Button("Refresh");

        HBox buttonBox = new HBox(10, addBtn, updateStockBtn, lowStockBtn, refreshBtn);
        buttonBox.setPadding(new Insets(10));

        addBtn.setOnAction(e -> openAddDialog());
        updateStockBtn.setOnAction(e -> openUpdateStockDialog());
        lowStockBtn.setOnAction(e -> filterLowStock());
        refreshBtn.setOnAction(e -> refreshTable());

        view.getChildren().addAll(buttonBox, table);
    }

    private void openAddDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Add Inventory Item");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField stockField = new TextField();
        stockField.setPromptText("Initial Stock");

        Button saveBtn = new Button("Save");
        Label status = new Label();

        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            int stock;
            try {
                stock = Integer.parseInt(stockField.getText().trim());
                int result = InventoryItemManagement.addNewInventoryItem(name, stock);
                if (result > 0) {
                    status.setText("Item added successfully!");
                    refreshTable();
                } else {
                    status.setText("Failed to add item.");
                }
            } catch (Exception ex) {
                status.setText("Invalid input.");
            }
        });

        VBox layout = new VBox(10, nameField, stockField, saveBtn, status);
        layout.setPadding(new Insets(20));
        dialog.setScene(new Scene(layout));
        dialog.show();
    }

    private void openUpdateStockDialog() {
        InventoryItem selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Stage dialog = new Stage();
        dialog.setTitle("Update Stock");

        TextField changeField = new TextField();
        changeField.setPromptText("Change in Stock (+ or -)");

        Button saveBtn = new Button("Update");
        Label status = new Label();

        saveBtn.setOnAction(e -> {
            try {
                int change = Integer.parseInt(changeField.getText().trim());
                boolean success = InventoryItemManagement.updateStock(selected.getId(), change);
                if (success) {
                    status.setText("Stock updated.");
                    refreshTable();
                } else {
                    status.setText("Failed to update.");
                }
            } catch (Exception ex) {
                status.setText("Invalid input.");
            }
        });

        VBox layout = new VBox(10, changeField, saveBtn, status);
        layout.setPadding(new Insets(20));
        dialog.setScene(new Scene(layout));
        dialog.show();
    }

    private void filterLowStock() {
        TextInputDialog dialog = new TextInputDialog("5");
        dialog.setTitle("Low Stock Filter");
        dialog.setHeaderText("Enter threshold value:");

        dialog.showAndWait().ifPresent(thresholdStr -> {
            try {
                int threshold = Integer.parseInt(thresholdStr);
                List<InventoryItem> items = InventoryItemManagement.getLowStockItems(threshold);
                table.getItems().setAll(items);
            } catch (Exception ignored) {}
        });
    }

    private void refreshTable() {
        List<InventoryItem> items = InventoryItemManagement.getAllInventoryItems();
        table.getItems().setAll(items);
    }

    public VBox getView() {
        return view;
    }
}
