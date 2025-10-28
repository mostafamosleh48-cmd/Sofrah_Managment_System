package com.example.sofrah_managment;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.text.SimpleDateFormat;
import java.util.List;

public class OrderUI {

    private TableView<Order> tableView;
    private ObservableList<Order> orderList;

    public VBox getView() {
        Label title = new Label("Order Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        tableView = new TableView<>();
        setupTable();
        loadOrders();

        Button btnAdd = new Button("Add Order");
        Button btnEdit = new Button("Edit Order");
        Button btnDelete = new Button("Delete Order");
        Button btnViewItems = new Button("View Items");
        Button btnRefresh = new Button("Refresh");

        Button btnFilterDate = new Button("Filter by Date");
        Button btnFilterCustomer = new Button("Filter by Customer");
        Button btnFilterType = new Button("Filter by Type");
        Button btnMonthlySales = new Button("Monthly Sales");
        Button btnProfitByType = new Button("Profit by Type");

        btnMonthlySales.setOnAction(e -> showMonthlySales());
        btnProfitByType.setOnAction(e -> showProfitByType());

        HBox reportBar = new HBox(10, btnMonthlySales, btnProfitByType);
        reportBar.setAlignment(Pos.CENTER);


        btnAdd.setOnAction(e -> handleAdd());
        btnEdit.setOnAction(e -> handleEdit());
        btnDelete.setOnAction(e -> handleDelete());
        btnViewItems.setOnAction(e -> handleViewItems());
        btnRefresh.setOnAction(e -> loadOrders());
        btnFilterDate.setOnAction(e -> filterByDate());
        btnFilterCustomer.setOnAction(e -> filterByCustomer());
        btnFilterType.setOnAction(e -> filterByType());

        HBox buttonBar = new HBox(10, btnAdd, btnEdit, btnDelete, btnViewItems, btnRefresh);
        buttonBar.setAlignment(Pos.CENTER);

        HBox filterBar = new HBox(10, btnFilterDate, btnFilterCustomer, btnFilterType);
        filterBar.setAlignment(Pos.CENTER);

        VBox root = new VBox(15, title, tableView, buttonBar, filterBar, reportBar);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f4f4f4;");
        return root;
    }

    private void setupTable() {
        TableColumn<Order, Integer> idCol = new TableColumn<>("Order ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Order, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(cellData -> {
            Customer customer = CustomerManagement.getCustomerByID(cellData.getValue().getCustomerId());
            return new javafx.beans.property.SimpleStringProperty(customer != null ? customer.getName() : "Unknown");
        });

        TableColumn<Order, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(cellData -> {
            String location = OrderManagement.getLocationForCustomer(cellData.getValue().getCustomerId());
            return new javafx.beans.property.SimpleStringProperty(location);
        });

        TableColumn<Order, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> {
            String status = OrderManagement.getStatusNameById(cellData.getValue().getOrderStatusId());
            return new javafx.beans.property.SimpleStringProperty(status);
        });

        TableColumn<Order, String> paymentCol = new TableColumn<>("Type");
        paymentCol.setCellValueFactory(cellData -> {
            String typeName = OrderManagement.getTypeNameById(cellData.getValue().getOrderTypeId());
            return new javafx.beans.property.SimpleStringProperty(typeName);
        });

        TableColumn<Order, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return new javafx.beans.property.SimpleStringProperty(
                    formatter.format(java.sql.Timestamp.valueOf(cellData.getValue().getDate())));
        });

        TableColumn<Order, String> itemNamesCol = new TableColumn<>("Items");
        itemNamesCol.setCellValueFactory(cellData -> {
            List<OrderItem> items = OrderManagement.getOrderItemsByOrderId(cellData.getValue().getId());
            StringBuilder sb = new StringBuilder();
            for (OrderItem item : items) {
                MenuItem menuItem = MenuItemManagement.getMenuItemById(item.getMenuItemId());
                if (menuItem != null) {
                    sb.append(menuItem.getItemName()).append(", ");
                }
            }
            String result = sb.toString();
            if (result.endsWith(", ")) result = result.substring(0, result.length() - 2);
            return new javafx.beans.property.SimpleStringProperty(result);
        });

        TableColumn<Order, String> quantitiesCol = new TableColumn<>("Quantities");
        quantitiesCol.setCellValueFactory(cellData -> {
            List<OrderItem> items = OrderManagement.getOrderItemsByOrderId(cellData.getValue().getId());
            StringBuilder sb = new StringBuilder();
            for (OrderItem item : items) {
                sb.append(item.getQuantity()).append(", ");
            }
            String result = sb.toString();
            if (result.endsWith(", ")) result = result.substring(0, result.length() - 2);
            return new javafx.beans.property.SimpleStringProperty(result);
        });

        //
        TableColumn<Order, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(cellData -> {
            List<OrderItem> items = OrderManagement.getOrderItemsByOrderId(cellData.getValue().getId());
            double total = 0.0;
            for (OrderItem item : items) {
                MenuItem menuItem = MenuItemManagement.getMenuItemById(item.getMenuItemId());
                if (menuItem != null) {
                    total += menuItem.getPrice() * item.getQuantity();
                }
            }
            return new SimpleStringProperty(String.format("%.2f", total));
        });


        tableView.getColumns().addAll(idCol, customerCol, locationCol, statusCol, paymentCol, dateCol, itemNamesCol, quantitiesCol, amountCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }


    private void loadOrders() {
        List<Order> orders = OrderManagement.getAllOrders();
        orderList = FXCollections.observableArrayList(orders);
        tableView.setItems(orderList);
    }

    private void handleAdd() {
        Dialog<Order> dialog = new Dialog<>();
        dialog.setTitle("Add Order");

        ComboBox<Customer> customerCombo = new ComboBox<>(FXCollections.observableArrayList(CustomerManagement.getAllCustomers()));
        ComboBox<Employee> employeeCombo = new ComboBox<>(FXCollections.observableArrayList(EmployeeManagement.getAllEmployees()));
        ComboBox<OrderStatus> statusCombo = new ComboBox<>(FXCollections.observableArrayList(OrderStatusManagement.getAllOrderStatuses()));
        ComboBox<OrderType> typeCombo = new ComboBox<>(FXCollections.observableArrayList(OrderTypeManagement.getAllOrderTypes()));
        ComboBox<MenuItem> itemCombo = new ComboBox<>(FXCollections.observableArrayList(MenuItemManagement.getAvailableMenuItems()));
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");

        ComboBox<Location> locationCombo = new ComboBox<>(FXCollections.observableArrayList(LocationManagement.getAllLocations()));

        // Section for extra items
        VBox extraItemsBox = new VBox(10);
        Button addExtraItemBtn = new Button("Add More Items");

        addExtraItemBtn.setOnAction(e -> {
            HBox row = new HBox(10);
            ComboBox<MenuItem> extraItemCombo = new ComboBox<>(FXCollections.observableArrayList(MenuItemManagement.getAvailableMenuItems()));
            TextField extraQtyField = new TextField();
            extraQtyField.setPromptText("Qty");
            row.getChildren().addAll(new Label("Item:"), extraItemCombo, new Label("Qty:"), extraQtyField);
            extraItemsBox.getChildren().add(row);
        });

        VBox content = new VBox(10,
                new Label("Customer:"), customerCombo,
                new Label("Employee:"), employeeCombo,
                new Label("Status:"), statusCombo,
                new Label("Type:"), typeCombo,
                new Label("Menu Item:"), itemCombo,
                new Label("Quantity:"), quantityField,
                new Label("Location:"), locationCombo,
                new Separator(),
                new Label("Additional Items (Optional):"), extraItemsBox,
                addExtraItemBtn
        );
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    if (customerCombo.getValue() == null || employeeCombo.getValue() == null ||
                            itemCombo.getValue() == null || locationCombo.getValue() == null)
                        throw new Exception("Missing selection");

                    int statusId = statusCombo.getValue().getId();
                    int typeId = typeCombo.getValue().getId();
                    int quantity = Integer.parseInt(quantityField.getText());

                    Order order = new Order(0,
                            customerCombo.getValue().getId(),
                            employeeCombo.getValue().getId(),
                            statusId,
                            typeId,
                            java.time.LocalDateTime.now());

                    if (OrderManagement.addOrder(order)) {
                        int orderId = OrderManagement.getLastInsertedOrderId();

                        // Insert selected location
                        CustomerLocation.linkCustomerToLocation(customerCombo.getValue().getId(), locationCombo.getValue().getId());

                        // Add main item
                        boolean success = OrderManagement.addOrderItem(new OrderItem(0, itemCombo.getValue().getId(), orderId, quantity));

                        // Add extra items
                        for (javafx.scene.Node node : extraItemsBox.getChildren()) {
                            if (node instanceof HBox row) {
                                ComboBox<MenuItem> extraItem = (ComboBox<MenuItem>) row.getChildren().get(1);
                                TextField extraQty = (TextField) row.getChildren().get(3);
                                if (extraItem.getValue() != null && !extraQty.getText().trim().isEmpty()) {
                                    int qty = Integer.parseInt(extraQty.getText().trim());
                                    OrderItem extra = new OrderItem(0, extraItem.getValue().getId(), orderId, qty);
                                    OrderManagement.addOrderItem(extra);
                                }
                            }
                        }

                        if (success) {
                            loadOrders();
                            showInfo("Order and items added successfully.");
                        } else {
                            showError("Order added but failed to add initial item.");
                        }
                    } else {
                        showError("Failed to add order.");
                    }

                } catch (Exception e) {
                    showError("Input error: " + e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
    }



    private void handleEdit() {
        Order selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select an order to edit.");
            return;
        }

        Dialog<Order> dialog = new Dialog<>();
        dialog.setTitle("Edit Order");

        List<Customer> customers = CustomerManagement.getAllCustomers();
        List<Employee> employees = EmployeeManagement.getAllEmployees();
        List<OrderStatus> statuses = OrderStatusManagement.getAllOrderStatuses();
        List<OrderType> types = OrderTypeManagement.getAllOrderTypes();
        List<MenuItem> items = MenuItemManagement.getAvailableMenuItems();
        List<Location> locations = LocationManagement.getAllLocations();

        ComboBox<Customer> customerCombo = new ComboBox<>(FXCollections.observableArrayList(customers));
        ComboBox<Employee> employeeCombo = new ComboBox<>(FXCollections.observableArrayList(employees));
        ComboBox<OrderStatus> statusCombo = new ComboBox<>(FXCollections.observableArrayList(statuses));
        ComboBox<OrderType> typeCombo = new ComboBox<>(FXCollections.observableArrayList(types));
        ComboBox<MenuItem> itemCombo = new ComboBox<>(FXCollections.observableArrayList(items));
        ComboBox<Location> locationCombo = new ComboBox<>(FXCollections.observableArrayList(locations));
        TextField quantityField = new TextField();

        // Extra items section
        VBox extraItemsBox = new VBox(10);
        Button addExtraItemBtn = new Button("Add More Items");

        addExtraItemBtn.setOnAction(e -> {
            HBox row = new HBox(10);
            ComboBox<MenuItem> extraItemCombo = new ComboBox<>(FXCollections.observableArrayList(items));
            TextField extraQtyField = new TextField();
            extraQtyField.setPromptText("Qty");
            row.getChildren().addAll(new Label("Item:"), extraItemCombo, new Label("Qty:"), extraQtyField);
            extraItemsBox.getChildren().add(row);
        });

        // Pre-fill
        customerCombo.setValue(customers.stream().filter(c -> c.getId() == selected.getCustomerId()).findFirst().orElse(null));
        employeeCombo.setValue(employees.stream().filter(e -> e.getId() == selected.getEmployeeId()).findFirst().orElse(null));
        statusCombo.setValue(statuses.stream().filter(s -> s.getId() == selected.getOrderStatusId()).findFirst().orElse(null));
        typeCombo.setValue(types.stream().filter(t -> t.getId() == selected.getOrderTypeId()).findFirst().orElse(null));

        List<OrderItem> orderItems = OrderManagement.getOrderItemsByOrderId(selected.getId());
        if (!orderItems.isEmpty()) {
            OrderItem firstItem = orderItems.get(0);
            itemCombo.setValue(items.stream().filter(i -> i.getId() == firstItem.getMenuItemId()).findFirst().orElse(null));
            quantityField.setText(String.valueOf(firstItem.getQuantity()));
            for (int i = 1; i < orderItems.size(); i++) {
                OrderItem item = orderItems.get(i);
                HBox row = new HBox(10);
                ComboBox<MenuItem> extraItemCombo = new ComboBox<>(FXCollections.observableArrayList(items));
                TextField extraQtyField = new TextField();
                extraQtyField.setPromptText("Qty");

                MenuItem menu = items.stream().filter(m -> m.getId() == item.getMenuItemId()).findFirst().orElse(null);
                extraItemCombo.setValue(menu);
                extraQtyField.setText(String.valueOf(item.getQuantity()));

                row.getChildren().addAll(new Label("Item:"), extraItemCombo, new Label("Qty:"), extraQtyField);
                extraItemsBox.getChildren().add(row);
            }
        }

        // Current location
        Location currentLocation = CustomerLocation.getPrimaryLocationForCustomer(selected.getCustomerId());
        if (currentLocation != null) {
            locationCombo.setValue(locations.stream().filter(loc -> loc.getId() == currentLocation.getId()).findFirst().orElse(null));
        }

        VBox content = new VBox(10,
                new Label("Customer:"), customerCombo,
                new Label("Employee:"), employeeCombo,
                new Label("Status:"), statusCombo,
                new Label("Type:"), typeCombo,
                new Label("Menu Item:"), itemCombo,
                new Label("Quantity:"), quantityField,
                new Label("Location:"), locationCombo,
                new Separator(),
                new Label("Additional Items:"), extraItemsBox,
                addExtraItemBtn
        );
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    if (customerCombo.getValue() == null || employeeCombo.getValue() == null ||
                            itemCombo.getValue() == null || locationCombo.getValue() == null ||
                            statusCombo.getValue() == null || typeCombo.getValue() == null) {
                        throw new Exception("Missing required fields.");
                    }

                    int quantity = Integer.parseInt(quantityField.getText());

                    Order updatedOrder = new Order(
                            selected.getId(),
                            customerCombo.getValue().getId(),
                            employeeCombo.getValue().getId(),
                            statusCombo.getValue().getId(),
                            typeCombo.getValue().getId(),
                            selected.getDate()
                    );

                    boolean orderUpdated = OrderManagement.updateOrder(updatedOrder);

                    int customerId = customerCombo.getValue().getId();
                    int locationId = locationCombo.getValue().getId();
                    CustomerLocation.unlinkCustomerFromAllLocations(customerId);
                    CustomerLocation.linkCustomerToLocation(customerId, locationId);
                    if (!CustomerLocation.isCustomerLinkedToLocation(customerId, locationId)) {
                        CustomerLocation.linkCustomerToLocation(customerId, locationId);
                    }

                    // Remove all old items
                    List<OrderItem> oldItems = OrderManagement.getOrderItemsByOrderId(updatedOrder.getId());
                    for (OrderItem item : oldItems) {
                        OrderManagement.deleteOrderItem(item.getOrderId(), item.getMenuItemId());
                    }

                    // Add main item
                    boolean itemAdded = OrderManagement.addOrderItem(
                            new OrderItem(0, itemCombo.getValue().getId(), updatedOrder.getId(), quantity)
                    );

                    // Add extra items
                    for (javafx.scene.Node node : extraItemsBox.getChildren()) {
                        if (node instanceof HBox row) {
                            ComboBox<MenuItem> extraItemCombo = (ComboBox<MenuItem>) row.getChildren().get(1);
                            TextField extraQtyField = (TextField) row.getChildren().get(3);
                            if (extraItemCombo.getValue() != null && !extraQtyField.getText().isEmpty()) {
                                int qty = Integer.parseInt(extraQtyField.getText().trim());
                                OrderItem extra = new OrderItem(0, extraItemCombo.getValue().getId(), updatedOrder.getId(), qty);
                                OrderManagement.addOrderItem(extra);
                            }
                        }
                    }

                    if (orderUpdated && itemAdded) {
                        loadOrders();
                        showInfo("Order updated successfully.");
                    } else {
                        showError("Update failed.");
                    }

                } catch (Exception e) {
                    showError("Error: " + e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
    }




    private void handleDelete() {
        Order selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select an order first.");
            return;
        }

        if (OrderManagement.deleteOrder(selected.getId())) {
            loadOrders();
            showInfo("Order deleted.");
        } else {
            showError("Delete failed.");
        }
    }

    private void handleViewItems() {
        Order selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select an order first.");
            return;
        }

        List<OrderItem> items = OrderManagement.getOrderItemsByOrderId(selected.getId());
        StringBuilder sb = new StringBuilder("Items:\n");
        for (OrderItem item : items) {
            MenuItem menuItem = MenuItemManagement.getMenuItemById(item.getMenuItemId());
            if (menuItem != null) {
                sb.append(menuItem.toString()).append(" - ").append(item.getQuantity()).append("\n");
            } else {
                sb.append("Unknown Item ID: ").append(item.getMenuItemId()).append(" - ").append(item.getQuantity()).append("\n");
            }
        }

        showInfo(sb.toString());
    }


    private void filterByDate() {
        DatePicker datePicker = new DatePicker();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Filter by Date");
        alert.setHeaderText("Pick a date:");
        alert.getDialogPane().setContent(datePicker);

        alert.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK && datePicker.getValue() != null) {
                List<Order> filtered = OrderManagement.getOrdersByDate(datePicker.getValue());
                orderList.setAll(filtered);
            }
        });
    }

    private void filterByCustomer() {
        ComboBox<Customer> box = new ComboBox<>(FXCollections.observableArrayList(CustomerManagement.getAllCustomers()));
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Filter by Customer");
        alert.setHeaderText("Select a customer:");
        alert.getDialogPane().setContent(box);

        alert.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK && box.getValue() != null) {
                List<Order> filtered = OrderManagement.getOrdersByCustomer(box.getValue().getId());
                orderList.setAll(filtered);
            }
        });
    }

    private void filterByType() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Filter by Type");
        dialog.setHeaderText("Enter Type Name:");
        dialog.showAndWait().ifPresent(typeName -> {
            List<Order> filtered = OrderManagement.getOrdersByType(typeName.trim());
            orderList.setAll(filtered);
        });


    }

    private void showMonthlySales() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Monthly Sales");
        dialog.setHeaderText("Enter month (yyyy-MM):");

        dialog.showAndWait().ifPresent(monthStr -> {
            try {
                List<String> result = OrderManagement.getTotalSalesByMonth(monthStr);
                if (result.isEmpty()) {
                    showInfo("No sales data found for this month.");
                } else {
                    showInfo(String.join("\n", result));
                }
            } catch (Exception e) {
                showError("Invalid input or error: " + e.getMessage());
            }
        });
    }

    private void showProfitByType() {
        List<String> result = OrderManagement.getProfitByOrderType();
        if (result.isEmpty()) {
            showInfo("No profit data available.");
        } else {
            showInfo(String.join("\n", result));
        }
    }


    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(msg);
        a.showAndWait();
    }
}
