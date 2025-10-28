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

public class MenuItemOrderUI {

    private TableView<SupplierOrder> supplierOrderTableView;
    private TableView<Supplier> supplierTableView;
    private ObservableList<SupplierOrder> supplierOrderList;
    private ObservableList<Supplier> supplierList;

    public VBox getView() {
        Label title = new Label("Supplier Ingredient Orders Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Create supplier section
        VBox supplierSection = createSupplierSection();

        // Create supplier orders section
        VBox ordersSection = createSupplierOrdersSection();

        // Main layout
        VBox root = new VBox(15, title, supplierSection, new Separator(), ordersSection);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f4f4f4;");
        return root;
    }

    private VBox createSupplierSection() {
        Label supplierTitle = new Label("Available Suppliers");
        supplierTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        supplierTableView = new TableView<>();
        setupSupplierTable();
        loadSuppliers();
        supplierTableView.setPrefHeight(200);

        Button btnCreateOrder = new Button("Create Ingredient Order from Selected Supplier");
        btnCreateOrder.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        btnCreateOrder.setOnAction(e -> handleCreateIngredientOrder());

        HBox supplierButtonBar = new HBox(10, btnCreateOrder);
        supplierButtonBar.setAlignment(Pos.CENTER);
        supplierButtonBar.setPadding(new Insets(10));

        VBox supplierSection = new VBox(10, supplierTitle, supplierTableView, supplierButtonBar);
        return supplierSection;
    }

    private VBox createSupplierOrdersSection() {
        Label orderTitle = new Label("Supplier Ingredient Orders");
        orderTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        supplierOrderTableView = new TableView<>();
        setupSupplierOrderTable();
        loadSupplierOrders();
        supplierOrderTableView.setPrefHeight(300);

        Button btnRefresh = new Button("Refresh Orders");
        Button btnViewDetails = new Button("View Order Details");
        Button btnMarkReceived = new Button("Mark as Received");

        btnRefresh.setOnAction(e -> loadSupplierOrders());
        btnViewDetails.setOnAction(e -> handleViewOrderDetails());
        btnMarkReceived.setOnAction(e -> handleMarkAsReceived());

        HBox orderButtonBar = new HBox(10, btnRefresh, btnViewDetails, btnMarkReceived);
        orderButtonBar.setAlignment(Pos.CENTER);
        orderButtonBar.setPadding(new Insets(10));

        VBox orderSection = new VBox(10, orderTitle, supplierOrderTableView, orderButtonBar);
        return orderSection;
    }

    private void setupSupplierTable() {
        TableColumn<Supplier, Integer> idCol = new TableColumn<>("Supplier ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(100);

        TableColumn<Supplier, String> nameCol = new TableColumn<>("Supplier Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<Supplier, String> phoneCol = new TableColumn<>("Phone Number");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNum"));
        phoneCol.setPrefWidth(150);

        TableColumn<Supplier, String> specialtyCol = new TableColumn<>("Specialty");
        specialtyCol.setCellValueFactory(cellData -> {
            // This would come from your supplier data - for now showing example
            return new javafx.beans.property.SimpleStringProperty("Food Ingredients");
        });
        specialtyCol.setPrefWidth(150);

        supplierTableView.getColumns().addAll(idCol, nameCol, phoneCol, specialtyCol);
        supplierTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupSupplierOrderTable() {
        TableColumn<SupplierOrder, Integer> orderIdCol = new TableColumn<>("Order ID");
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderIdCol.setPrefWidth(80);

        TableColumn<SupplierOrder, String> supplierCol = new TableColumn<>("Supplier");
        supplierCol.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        supplierCol.setPrefWidth(150);

        TableColumn<SupplierOrder, String> dateCol = new TableColumn<>("Order Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        dateCol.setPrefWidth(100);

        TableColumn<SupplierOrder, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);

        TableColumn<SupplierOrder, Double> totalCol = new TableColumn<>("Total Amount");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        totalCol.setPrefWidth(120);

        TableColumn<SupplierOrder, String> expectedCol = new TableColumn<>("Expected Delivery");
        expectedCol.setCellValueFactory(new PropertyValueFactory<>("expectedDelivery"));
        expectedCol.setPrefWidth(130);

        // Format currency column
        totalCol.setCellFactory(col -> new TableCell<SupplierOrder, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", amount));
                }
            }
        });

        supplierOrderTableView.getColumns().addAll(orderIdCol, supplierCol, dateCol, statusCol, totalCol, expectedCol);
        supplierOrderTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void handleCreateIngredientOrder() {
        Supplier selectedSupplier = supplierTableView.getSelectionModel().getSelectedItem();
        if (selectedSupplier == null) {
            showError("Please select a supplier to create an ingredient order.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Create Ingredient Order - " + selectedSupplier.getName());
        dialog.setHeaderText("Order ingredients from: " + selectedSupplier.getName());

        // Create form fields
        DatePicker orderDatePicker = new DatePicker(LocalDate.now());
        DatePicker expectedDeliveryPicker = new DatePicker(LocalDate.now().plusDays(3));

        ComboBox<Integer> employeeComboBox = new ComboBox<>();
        employeeComboBox.setPromptText("Select Ordering Employee");
        // Populate with actual employee IDs from your database
        for (int i = 1; i <= 10; i++) {
            employeeComboBox.getItems().add(i);
        }

        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Enter order notes (optional)");
        notesArea.setPrefRowCount(3);

        // Table for selected ingredients
        TableView<SelectedIngredient> ingredientsTable = new TableView<>();
        setupSelectedIngredientsTable(ingredientsTable);

        ObservableList<SelectedIngredient> selectedIngredients = FXCollections.observableArrayList();
        ingredientsTable.setItems(selectedIngredients);
        ingredientsTable.setPrefHeight(200);

        // Form for adding ingredients
        TextField ingredientNameField = new TextField();
        ingredientNameField.setPromptText("Ingredient Name (e.g., Rice, Cola, Chicken)");

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");

        ComboBox<String> unitComboBox = new ComboBox<>();
        unitComboBox.getItems().addAll("kg", "liters", "pieces", "boxes", "bags", "cans");
        unitComboBox.setPromptText("Unit");

        TextField unitPriceField = new TextField();
        unitPriceField.setPromptText("Unit Price");

        Button addIngredientBtn = new Button("Add Ingredient");
        Button removeIngredientBtn = new Button("Remove Selected");

        addIngredientBtn.setOnAction(e -> {
            String ingredientName = ingredientNameField.getText().trim();
            String quantityText = quantityField.getText().trim();
            String unit = unitComboBox.getValue();
            String priceText = unitPriceField.getText().trim();

            if (ingredientName.isEmpty() || quantityText.isEmpty() || unit == null || priceText.isEmpty()) {
                showError("Please fill in all ingredient fields.");
                return;
            }

            try {
                double quantity = Double.parseDouble(quantityText);
                double unitPrice = Double.parseDouble(priceText);

                if (quantity <= 0 || unitPrice <= 0) {
                    showError("Quantity and price must be positive numbers.");
                    return;
                }

                // Check if ingredient already added
                boolean alreadyExists = selectedIngredients.stream()
                        .anyMatch(ing -> ing.getName().equalsIgnoreCase(ingredientName));

                if (alreadyExists) {
                    showError("This ingredient is already added to the order.");
                    return;
                }

                SelectedIngredient ingredient = new SelectedIngredient(
                        ingredientName, quantity, unit, unitPrice
                );
                selectedIngredients.add(ingredient);

                // Clear fields
                ingredientNameField.clear();
                quantityField.clear();
                unitComboBox.getSelectionModel().clearSelection();
                unitPriceField.clear();

            } catch (NumberFormatException ex) {
                showError("Please enter valid numbers for quantity and price.");
            }
        });

        removeIngredientBtn.setOnAction(e -> {
            SelectedIngredient selected = ingredientsTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedIngredients.remove(selected);
            } else {
                showError("Please select an ingredient to remove.");
            }
        });

        // Layout
        GridPane orderForm = new GridPane();
        orderForm.setHgap(10);
        orderForm.setVgap(10);

        orderForm.add(new Label("Order Date:"), 0, 0);
        orderForm.add(orderDatePicker, 1, 0);

        orderForm.add(new Label("Expected Delivery:"), 0, 1);
        orderForm.add(expectedDeliveryPicker, 1, 1);

        orderForm.add(new Label("Ordering Employee:"), 0, 2);
        orderForm.add(employeeComboBox, 1, 2);

        GridPane ingredientForm = new GridPane();
        ingredientForm.setHgap(10);
        ingredientForm.setVgap(5);
        ingredientForm.add(new Label("Ingredient:"), 0, 0);
        ingredientForm.add(ingredientNameField, 1, 0);
        ingredientForm.add(new Label("Quantity:"), 0, 1);
        ingredientForm.add(quantityField, 1, 1);
        ingredientForm.add(new Label("Unit:"), 2, 1);
        ingredientForm.add(unitComboBox, 3, 1);
        ingredientForm.add(new Label("Unit Price:"), 0, 2);
        ingredientForm.add(unitPriceField, 1, 2);

        HBox ingredientButtons = new HBox(10, addIngredientBtn, removeIngredientBtn);
        ingredientButtons.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(15,
                new Label("Order Information:"),
                orderForm,
                new Separator(),
                new Label("Ingredients to Order:"),
                ingredientForm,
                ingredientButtons,
                ingredientsTable,
                new Separator(),
                new Label("Notes:"), notesArea
        );
        content.setPadding(new Insets(15));

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(800, 600);

        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Enable/disable OK button based on required fields
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.disableProperty().bind(
                javafx.beans.binding.Bindings.createBooleanBinding(() ->
                                employeeComboBox.getValue() == null ||
                                        selectedIngredients.isEmpty(),
                        employeeComboBox.valueProperty(),
                        selectedIngredients
                )
        );

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    double totalAmount = selectedIngredients.stream()
                            .mapToDouble(ing -> ing.getQuantity() * ing.getUnitPrice())
                            .sum();

                    // Create supplier order (you would implement this in your management class)
                    SupplierOrder newOrder = new SupplierOrder(
                            generateOrderId(),
                            selectedSupplier.getName(),
                            orderDatePicker.getValue().toString(),
                            "Pending",
                            totalAmount,
                            expectedDeliveryPicker.getValue().toString(),
                            selectedIngredients,
                            notesArea.getText()
                    );

                    // Add to the observable list (in real implementation, save to database)
                    supplierOrderList.add(newOrder);

                    showInfo("Ingredient Order created successfully!\n" +
                            "Supplier: " + selectedSupplier.getName() + "\n" +
                            "Items: " + selectedIngredients.size() + "\n" +
                            "Total: $" + String.format("%.2f", totalAmount) + "\n" +
                            "Expected Delivery: " + expectedDeliveryPicker.getValue());

                    loadSupplierOrders(); // Refresh the order table

                } catch (Exception ex) {
                    showError("Error creating order: " + ex.getMessage());
                }
            }
            return button;
        });

        dialog.showAndWait();
    }

    private void setupSelectedIngredientsTable(TableView<SelectedIngredient> table) {
        TableColumn<SelectedIngredient, String> nameCol = new TableColumn<>("Ingredient");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(150);

        TableColumn<SelectedIngredient, Double> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setPrefWidth(80);

        TableColumn<SelectedIngredient, String> unitCol = new TableColumn<>("Unit");
        unitCol.setCellValueFactory(new PropertyValueFactory<>("unit"));
        unitCol.setPrefWidth(60);

        TableColumn<SelectedIngredient, Double> unitPriceCol = new TableColumn<>("Unit Price");
        unitPriceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        unitPriceCol.setPrefWidth(100);

        TableColumn<SelectedIngredient, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(cellData -> {
            SelectedIngredient ingredient = cellData.getValue();
            return new javafx.beans.property.SimpleDoubleProperty(
                    ingredient.getQuantity() * ingredient.getUnitPrice()).asObject();
        });
        totalCol.setPrefWidth(100);

        // Format currency columns
        unitPriceCol.setCellFactory(col -> new TableCell<SelectedIngredient, Double>() {
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

        totalCol.setCellFactory(col -> new TableCell<SelectedIngredient, Double>() {
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

        table.getColumns().addAll(nameCol, quantityCol, unitCol, unitPriceCol, totalCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadSuppliers() {
        List<Supplier> suppliers = SupplierManagement.getAllSuppliers();
        supplierList = FXCollections.observableArrayList(suppliers);
        supplierTableView.setItems(supplierList);
    }

    private void loadSupplierOrders() {
        // In real implementation, load from database
        // For now, initialize with sample data if empty
        if (supplierOrderList == null) {
            supplierOrderList = FXCollections.observableArrayList();
            // Add sample data
            supplierOrderList.add(new SupplierOrder(1, "Al-Mashhadawi", "2024-01-15", "Delivered", 450.00, "2024-01-18", null, "Rice and spices order"));
            supplierOrderList.add(new SupplierOrder(2, "Fresh Foods Co.", "2024-01-16", "Pending", 320.50, "2024-01-19", null, "Vegetables and fruits"));
        }
        supplierOrderTableView.setItems(supplierOrderList);
    }

    private void handleViewOrderDetails() {
        SupplierOrder selectedOrder = supplierOrderTableView.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showError("Please select an order to view details.");
            return;
        }

        Alert detailsAlert = new Alert(Alert.AlertType.INFORMATION);
        detailsAlert.setTitle("Supplier Order Details");
        detailsAlert.setHeaderText("Order ID: " + selectedOrder.getOrderId());

        String details = "Supplier: " + selectedOrder.getSupplierName() + "\n" +
                "Order Date: " + selectedOrder.getOrderDate() + "\n" +
                "Status: " + selectedOrder.getStatus() + "\n" +
                "Total Amount: $" + String.format("%.2f", selectedOrder.getTotalAmount()) + "\n" +
                "Expected Delivery: " + selectedOrder.getExpectedDelivery() + "\n" +
                "Notes: " + (selectedOrder.getNotes() != null ? selectedOrder.getNotes() : "None");

        detailsAlert.setContentText(details);
        detailsAlert.showAndWait();
    }

    private void handleMarkAsReceived() {
        SupplierOrder selectedOrder = supplierOrderTableView.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showError("Please select an order to mark as received.");
            return;
        }

        if ("Delivered".equals(selectedOrder.getStatus())) {
            showInfo("This order is already marked as delivered.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delivery");
        confirmAlert.setHeaderText("Mark Order as Received");
        confirmAlert.setContentText("Are you sure you want to mark this order as received?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                selectedOrder.setStatus("Delivered");
                supplierOrderTableView.refresh();
                showInfo("Order marked as delivered successfully!");
            }
        });
    }

    private int generateOrderId() {
        return supplierOrderList.size() + 1;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }

    // Inner class for selected ingredients in supplier order
    public static class SelectedIngredient {
        private String name;
        private double quantity;
        private String unit;
        private double unitPrice;

        public SelectedIngredient(String name, double quantity, String unit, double unitPrice) {
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
            this.unitPrice = unitPrice;
        }

        // Getters and setters
        public String getName() { return name; }
        public double getQuantity() { return quantity; }
        public String getUnit() { return unit; }
        public double getUnitPrice() { return unitPrice; }

        public void setName(String name) { this.name = name; }
        public void setQuantity(double quantity) { this.quantity = quantity; }
        public void setUnit(String unit) { this.unit = unit; }
        public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    }

    // Inner class for supplier orders
    public static class SupplierOrder {
        private int orderId;
        private String supplierName;
        private String orderDate;
        private String status;
        private double totalAmount;
        private String expectedDelivery;
        private ObservableList<SelectedIngredient> ingredients;
        private String notes;

        public SupplierOrder(int orderId, String supplierName, String orderDate, String status,
                             double totalAmount, String expectedDelivery,
                             ObservableList<SelectedIngredient> ingredients, String notes) {
            this.orderId = orderId;
            this.supplierName = supplierName;
            this.orderDate = orderDate;
            this.status = status;
            this.totalAmount = totalAmount;
            this.expectedDelivery = expectedDelivery;
            this.ingredients = ingredients;
            this.notes = notes;
        }

        // Getters and setters
        public int getOrderId() { return orderId; }
        public String getSupplierName() { return supplierName; }
        public String getOrderDate() { return orderDate; }
        public String getStatus() { return status; }
        public double getTotalAmount() { return totalAmount; }
        public String getExpectedDelivery() { return expectedDelivery; }
        public ObservableList<SelectedIngredient> getIngredients() { return ingredients; }
        public String getNotes() { return notes; }

        public void setStatus(String status) { this.status = status; }
        public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    }
}