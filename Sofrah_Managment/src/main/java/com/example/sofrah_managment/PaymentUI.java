package com.example.sofrah_managment;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.List;

public class PaymentUI {

    private TableView<Payment> tableView;
    private ObservableList<Payment> paymentList;

    public VBox getView() {
        Label title = new Label("Payment Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        tableView = new TableView<>();
        setupTable();
        loadPayments();

        Button btnAdd = new Button("Add Payment");
        Button btnEdit = new Button("Edit Payment");
        Button btnRefresh = new Button("Refresh");

        btnAdd.setOnAction(e -> handleAdd());
        btnEdit.setOnAction(e -> handleEdit());
        btnRefresh.setOnAction(e -> loadPayments());

        HBox buttonBar = new HBox(10, btnAdd, btnEdit, btnRefresh);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setPadding(new Insets(15));

        VBox root = new VBox(15, title, tableView, buttonBar);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f4f4f4;");
        return root;
    }

    private void setupTable() {
        TableColumn<Payment, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Payment, Integer> orderIdCol = new TableColumn<>("Order ID");
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));

        TableColumn<Payment, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Payment, String> methodCol = new TableColumn<>("Method");
        methodCol.setCellValueFactory(new PropertyValueFactory<>("paymentName"));

        tableView.getColumns().addAll(idCol, orderIdCol, amountCol, methodCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadPayments() {
        List<Payment> payments = PaymentManagement.getAllPayments();
        paymentList = FXCollections.observableArrayList(payments);
        tableView.setItems(paymentList);
    }

    private void handleAdd() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Payment");

        TextField txtOrderId = new TextField();
        TextField txtAmount = new TextField();
        ComboBox<String> methodCombo = new ComboBox<>(FXCollections.observableArrayList("Cash", "Visa", "MasterCard"));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.add(new Label("Order ID:"), 0, 0);
        grid.add(txtOrderId, 1, 0);
        grid.add(new Label("Amount:"), 0, 1);
        grid.add(txtAmount, 1, 1);
        grid.add(new Label("Method:"), 0, 2);
        grid.add(methodCombo, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    int orderId = Integer.parseInt(txtOrderId.getText().trim());
                    double amount = Double.parseDouble(txtAmount.getText().trim());
                    String method = methodCombo.getValue();

                    if (amount < 0 || method == null || method.isEmpty()) {
                        throw new IllegalArgumentException("Invalid input");
                    }

                    Payment payment = new Payment(0, orderId, amount, method);
                    boolean success = PaymentManagement.addPayment(payment);

                    if (success) {
                        loadPayments();
                        showAlert("Success", "Payment added successfully.");
                    } else {
                        showAlert("Error", "Failed to add payment.");
                    }
                } catch (Exception e) {
                    showAlert("Input Error", "Please enter valid values.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void handleEdit() {
        Payment selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a payment to edit.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Payment");

        TextField txtOrderId = new TextField(String.valueOf(selected.getOrderId()));
        TextField txtAmount = new TextField(String.valueOf(selected.getAmount()));
        ComboBox<String> methodCombo = new ComboBox<>(FXCollections.observableArrayList("Cash", "Visa", "MasterCard"));
        methodCombo.setValue(selected.getPaymentName());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.add(new Label("Order ID:"), 0, 0);
        grid.add(txtOrderId, 1, 0);
        grid.add(new Label("Amount:"), 0, 1);
        grid.add(txtAmount, 1, 1);
        grid.add(new Label("Method:"), 0, 2);
        grid.add(methodCombo, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    int orderId = Integer.parseInt(txtOrderId.getText().trim());
                    double amount = Double.parseDouble(txtAmount.getText().trim());
                    String method = methodCombo.getValue();

                    if (amount < 0 || method == null || method.isEmpty()) {
                        throw new IllegalArgumentException("Invalid input");
                    }

                    selected.setOrderId(orderId);
                    selected.setAmount(amount);
                    selected.setPaymentName(method);

                    boolean success = PaymentManagement.updatePayment(selected);
                    if (success) {
                        loadPayments();
                        showAlert("Success", "Payment updated successfully.");
                    } else {
                        showAlert("Error", "Update failed.");
                    }
                } catch (Exception e) {
                    showAlert("Input Error", "Please enter valid values.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
