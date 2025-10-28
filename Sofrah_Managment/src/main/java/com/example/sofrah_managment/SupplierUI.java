// Enhanced SupplierUI.java with Order Creation functionality
package com.example.sofrah_managment;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class SupplierUI {

    private TableView<Supplier> tableView;
    private ObservableList<Supplier> supplierList;

    public VBox getView() {
        Label title = new Label("Supplier Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        tableView = new TableView<>();
        setupTable();
        loadSuppliers();

        Button btnAdd = new Button("Add Supplier");
        Button btnEdit = new Button("Edit Supplier");
        Button btnDelete = new Button("Delete Supplier");
        Button btnRefresh = new Button("Refresh");
        Button btnCreateOrder = new Button("Create Order"); // NEW BUTTON
        Button btnOrdersByDate = new Button("Orders by Date");
        Button btnSuppliersByIngredient = new Button("Suppliers by Ingredient");

        // Style the new button
        btnCreateOrder.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");

        btnAdd.setOnAction(e -> handleAdd());
        btnEdit.setOnAction(e -> handleEdit());
        btnDelete.setOnAction(e -> handleDelete());
        btnRefresh.setOnAction(e -> loadSuppliers());
        btnCreateOrder.setOnAction(e -> handleCreateOrder()); // NEW ACTION
        btnOrdersByDate.setOnAction(e -> handleOrdersByDate());
        btnSuppliersByIngredient.setOnAction(e -> handleSuppliersByIngredient());

        // Updated button bar with the new button
        HBox buttonBar = new HBox(10, btnAdd, btnEdit, btnDelete, btnRefresh,
                btnCreateOrder, btnOrdersByDate, btnSuppliersByIngredient);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setPadding(new Insets(15));

        VBox root = new VBox(15, title, tableView, buttonBar);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f4f4f4;");
        return root;
    }

    // NEW METHOD: Handle order creation
    private void handleCreateOrder() {
        Supplier selectedSupplier = tableView.getSelectionModel().getSelectedItem();
        if (selectedSupplier == null) {
            showError("Please select a supplier to create an order.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Create Order from " + selectedSupplier.getName());
        dialog.setHeaderText("Create a new order for supplier: " + selectedSupplier.getName());

        // Create form fields
        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextArea orderDetailsArea = new TextArea();
        orderDetailsArea.setPromptText("Enter order details/notes (optional)");
        orderDetailsArea.setPrefRowCount(3);

        // Table for order items
        TableView<OrderItem> itemsTable = new TableView<>();
        setupOrderItemsTable(itemsTable);

        ObservableList<OrderItem> orderItems = FXCollections.observableArrayList();
        itemsTable.setItems(orderItems);
        itemsTable.setPrefHeight(200);

        // Form for adding items
        TextField componentField = new TextField();
        componentField.setPromptText("Component/Ingredient name");

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");

        TextField unitPriceField = new TextField();
        unitPriceField.setPromptText("Unit price");

        Button addItemBtn = new Button("Add Item");
        Button removeItemBtn = new Button("Remove Selected");

        addItemBtn.setOnAction(e -> {
            String component = componentField.getText().trim();
            String quantityText = quantityField.getText().trim();
            String priceText = unitPriceField.getText().trim();

            if (component.isEmpty() || quantityText.isEmpty() || priceText.isEmpty()) {
                showError("Please fill all fields to add an item.");
                return;
            }

            try {
                double quantity = Double.parseDouble(quantityText);
                double unitPrice = Double.parseDouble(priceText);

                if (quantity <= 0 || unitPrice <= 0) {
                    showError("Quantity and unit price must be positive numbers.");
                    return;
                }

                OrderItem item = new OrderItem(component, quantity, unitPrice);
                orderItems.add(item);

                // Clear fields
                componentField.clear();
                quantityField.clear();
                unitPriceField.clear();

            } catch (NumberFormatException ex) {
                showError("Please enter valid numbers for quantity and price.");
            }
        });

        removeItemBtn.setOnAction(e -> {
            OrderItem selected = itemsTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                orderItems.remove(selected);
            } else {
                showError("Please select an item to remove.");
            }
        });

        // Layout
        GridPane itemForm = new GridPane();
        itemForm.setHgap(10);
        itemForm.setVgap(5);
        itemForm.add(new Label("Component:"), 0, 0);
        itemForm.add(componentField, 1, 0);
        itemForm.add(new Label("Quantity:"), 2, 0);
        itemForm.add(quantityField, 3, 0);
        itemForm.add(new Label("Unit Price:"), 4, 0);
        itemForm.add(unitPriceField, 5, 0);

        HBox itemButtons = new HBox(10, addItemBtn, removeItemBtn);
        itemButtons.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(15,
                new Label("Order Date:"), datePicker,
                new Separator(),
                new Label("Order Items:"),
                itemForm,
                itemButtons,
                itemsTable,
                new Separator(),
                new Label("Order Details/Notes:"), orderDetailsArea
        );
        content.setPadding(new Insets(15));

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(800, 600);

        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Enable/disable OK button based on items
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.disableProperty().bind(
                javafx.beans.binding.Bindings.isEmpty(orderItems)
        );

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    LocalDate orderDate = datePicker.getValue();
                    String orderDetails = orderDetailsArea.getText().trim();

                    // Calculate total price
                    double totalPrice = orderItems.stream()
                            .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                            .sum();

                    // Create the order
                    boolean success = SupplierManagement.createSupplierOrder(
                            selectedSupplier.getId(),
                            orderDate,
                            totalPrice,
                            orderDetails,
                            new ArrayList<>(orderItems)
                    );

                    if (success) {
                        showInfo("Order created successfully!\nTotal: $" +
                                String.format("%.2f", totalPrice));
                    } else {
                        showError("Failed to create order.");
                    }
                } catch (Exception ex) {
                    showError("Error creating order: " + ex.getMessage());
                }
            }
            return button;
        });

        dialog.showAndWait();
    }

    // NEW METHOD: Setup order items table
    private void setupOrderItemsTable(TableView<OrderItem> table) {
        TableColumn<OrderItem, String> componentCol = new TableColumn<>("Component");
        componentCol.setCellValueFactory(new PropertyValueFactory<>("componentName"));
        componentCol.setPrefWidth(200);

        TableColumn<OrderItem, Double> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setPrefWidth(100);

        TableColumn<OrderItem, Double> unitPriceCol = new TableColumn<>("Unit Price");
        unitPriceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        unitPriceCol.setPrefWidth(100);

        TableColumn<OrderItem, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(cellData -> {
            OrderItem item = cellData.getValue();
            return new javafx.beans.property.SimpleDoubleProperty(
                    item.getQuantity() * item.getUnitPrice()).asObject();
        });
        totalCol.setPrefWidth(100);

        // Format currency columns
        unitPriceCol.setCellFactory(col -> new TableCell<OrderItem, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", price));
                }
            }
        });

        totalCol.setCellFactory(col -> new TableCell<OrderItem, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", total));
                }
            }
        });

        table.getColumns().addAll(componentCol, quantityCol, unitPriceCol, totalCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // NEW INNER CLASS: Order Item
    public static class OrderItem {
        private String componentName;
        private double quantity;
        private double unitPrice;

        public OrderItem(String componentName, double quantity, double unitPrice) {
            this.componentName = componentName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public String getComponentName() { return componentName; }
        public double getQuantity() { return quantity; }
        public double getUnitPrice() { return unitPrice; }

        public void setComponentName(String componentName) { this.componentName = componentName; }
        public void setQuantity(double quantity) { this.quantity = quantity; }
        public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    }

    // EXISTING METHODS (unchanged)
    private void setupTable() {
        TableColumn<Supplier, Integer> idCol = new TableColumn<>("Supplier ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Supplier, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Supplier, String> phoneCol = new TableColumn<>("Phone Number");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNum"));

        tableView.getColumns().addAll(idCol, nameCol, phoneCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadSuppliers() {
        List<Supplier> suppliers = SupplierManagement.getAllSuppliers();
        supplierList = FXCollections.observableArrayList(suppliers);
        tableView.setItems(supplierList);
    }

    private void handleOrdersByDate() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Orders by Date");

        TextField dateField = new TextField();
        dateField.setPromptText("YYYY-MM-DD");

        VBox content = new VBox(10,
                new Label("Enter Date (YYYY-MM-DD):"), dateField
        );
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                String date = dateField.getText().trim();
                if (!date.isEmpty()) {
                    showOrdersByDate(date);
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showOrdersByDate(String date) {
        List<SupplierManagement.SupplierOrder> orders = SupplierManagement.getSupplierOrdersByDate(date);

        if (orders.isEmpty()) {
            showInfo("No orders found for date: " + date);
            return;
        }

        Dialog<Void> ordersDialog = new Dialog<>();
        ordersDialog.setTitle("Supplier Orders for " + date);

        TableView<SupplierManagement.SupplierOrder> ordersTable = new TableView<>();

        TableColumn<SupplierManagement.SupplierOrder, Integer> orderIdCol = new TableColumn<>("Order ID");
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));

        TableColumn<SupplierManagement.SupplierOrder, String> supplierCol = new TableColumn<>("Supplier");
        supplierCol.setCellValueFactory(new PropertyValueFactory<>("supplierName"));

        TableColumn<SupplierManagement.SupplierOrder, Double> priceCol = new TableColumn<>("Total Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        TableColumn<SupplierManagement.SupplierOrder, String> detailsCol = new TableColumn<>("Details");
        detailsCol.setCellValueFactory(new PropertyValueFactory<>("orderDetails"));

        ordersTable.getColumns().addAll(orderIdCol, supplierCol, priceCol, detailsCol);
        ordersTable.setItems(FXCollections.observableArrayList(orders));
        ordersTable.setPrefHeight(300);

        VBox ordersContent = new VBox(10,
                new Label("Orders found: " + orders.size()),
                ordersTable
        );
        ordersContent.setPadding(new Insets(10));

        ordersDialog.getDialogPane().setContent(ordersContent);
        ordersDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        ordersDialog.showAndWait();
    }

    private void handleSuppliersByIngredient() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Suppliers by Ingredient");

        TextField ingredientField = new TextField();
        ingredientField.setPromptText("Ingredient Name");

        VBox content = new VBox(10,
                new Label("Enter Ingredient Name:"), ingredientField
        );
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                String ingredient = ingredientField.getText().trim();
                if (!ingredient.isEmpty()) {
                    showSuppliersByIngredient(ingredient);
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showSuppliersByIngredient(String ingredientName) {
        List<Supplier> suppliers = SupplierManagement.getSuppliersByIngredient(ingredientName);
        supplierList = FXCollections.observableArrayList(suppliers);
        tableView.setItems(supplierList);

        if (suppliers.isEmpty()) {
            showInfo("No suppliers found for ingredient: " + ingredientName);
        } else {
            showInfo("Found " + suppliers.size() + " supplier(s) for ingredient: " + ingredientName);
        }
    }

    private void handleAdd() {
        Dialog<Supplier> dialog = new Dialog<>();
        dialog.setTitle("Add Supplier");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");

        VBox content = new VBox(10,
                new Label("Name:"), nameField,
                new Label("Phone Number:"), phoneField
        );
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    String name = nameField.getText().trim();
                    String phone = phoneField.getText().trim();

                    Supplier supplier = new Supplier(0, name, phone);
                    SupplierManagement supplierMgmt = new SupplierManagement();
                    int newId = supplierMgmt.addSupplier(supplier);
                    if (newId > 0) {
                        loadSuppliers();
                        showInfo("Supplier added successfully.");
                    } else {
                        showError("Failed to add supplier.");
                    }
                } catch (Exception e) {
                    showError("Error adding supplier: " + e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void handleEdit() {
        Supplier selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a supplier to edit.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Supplier");

        TextField nameField = new TextField(selected.getName());
        TextField phoneField = new TextField(selected.getPhoneNum());

        VBox content = new VBox(10,
                new Label("Name:"), nameField,
                new Label("Phone Number:"), phoneField
        );
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                selected.setName(nameField.getText().trim());
                selected.setPhoneNum(phoneField.getText().trim());

                boolean success = SupplierManagement.updateSupplier(selected);
                if (success) {
                    loadSuppliers();
                    showInfo("Supplier updated successfully.");
                } else {
                    showError("Failed to update supplier.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void handleDelete() {
        Supplier selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a supplier to delete.");
            return;
        }

        boolean success = SupplierManagement.deleteSupplier(selected.getId());
        if (success) {
            loadSuppliers();
            showInfo("Supplier deleted successfully.");
        } else {
            showError("Failed to delete supplier.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }
}