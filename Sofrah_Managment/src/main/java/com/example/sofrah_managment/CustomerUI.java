package com.example.sofrah_managment;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.List;

public class CustomerUI {

    private TableView<Customer> tableView;
    private ObservableList<Customer> customerList;

    public VBox getView() {
        Label title = new Label("Customer Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        tableView = new TableView<>();
        setupTable();
        loadCustomers();

        Button btnAdd = new Button("Add Customer");
        Button btnEdit = new Button("Edit Customer");
        Button btnDelete = new Button("Delete Customer");
        Button btnRefresh = new Button("Refresh");
        Button deliveryCustomersBtn = new Button("Delivery Customers");
        Button showAllBtn = new Button("Show All");

        btnAdd.setOnAction(e -> handleAdd());
        btnEdit.setOnAction(e -> handleEdit());
        btnDelete.setOnAction(e -> handleDelete());
        btnRefresh.setOnAction(e -> loadCustomers());
        deliveryCustomersBtn.setOnAction(e -> handleDeliveryCustomers());
        showAllBtn.setOnAction(e -> loadCustomers());

        HBox buttonBar = new HBox(10, btnAdd, btnEdit, btnDelete, btnRefresh, deliveryCustomersBtn, showAllBtn);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setPadding(new Insets(15));

        VBox root = new VBox(15, title, tableView, buttonBar);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f4f4f4;");
        return root;
    }

    private void setupTable() {
        TableColumn<Customer, Integer> idCol = new TableColumn<>("Customer ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Customer, String> phoneCol = new TableColumn<>("Phone Number");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        TableColumn<Customer, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(cellData -> {
            List<Location> locs = CustomerLocation.getLocationsForCustomer(cellData.getValue().getId());
            if (locs.isEmpty()) return new SimpleStringProperty("Not Set");
            Location loc = locs.get(0); // just show the first
            return new SimpleStringProperty(loc.getCity() + ", " + loc.getStreet());
        });


        tableView.getColumns().addAll(idCol, nameCol, phoneCol, locationCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


    }

    private void loadCustomers() {
        List<Customer> customers = CustomerManagement.getAllCustomers();
        customerList = FXCollections.observableArrayList(customers);
        tableView.setItems(customerList);
    }

    private void handleDeliveryCustomers() {
        List<Customer> deliveryCustomers = CustomerManagement.getCustomersWithDeliveryOrders();
        customerList = FXCollections.observableArrayList(deliveryCustomers);
        tableView.setItems(customerList);

        if (deliveryCustomers.isEmpty()) {
            showInfo("No customers found with delivery orders.");
        } else {
            showInfo("Found " + deliveryCustomers.size() + " customer(s) with delivery orders.");
        }
    }

    private void handleAdd() {
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle("Add Customer");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");

        TextField cityField = new TextField();
        cityField.setPromptText("City");

        TextField streetField = new TextField();
        streetField.setPromptText("Street");

        VBox content = new VBox(10,
                new Label("Name:"), nameField,
                new Label("Phone Number:"), phoneField,
                new Label("City:"), cityField,
                new Label("Street:"), streetField
        );
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    String name = nameField.getText().trim();
                    String phone = phoneField.getText().trim();
                    String city = cityField.getText().trim();
                    String street = streetField.getText().trim();

                    if (name.isEmpty() || phone.isEmpty() || city.isEmpty() || street.isEmpty())
                        throw new Exception("All fields are required.");

                    Customer customer = new Customer(0, name, phone);
                    boolean customerAdded = CustomerManagement.addCustomer(customer);

                    if (customerAdded) {
                        int customerId = CustomerManagement.getLastInsertedCustomerId(); // You need to implement this
                        Location location = new Location(0, city, street);
                        int locationId = LocationManagement.addOrGetLocationId(location); // You need to implement this

                        boolean linked = CustomerLocation.linkCustomerToLocation(customerId, locationId);

                        if (linked) {
                            loadCustomers();
                            showInfo("Customer and location added successfully.");
                        } else {
                            showError("Customer added, but failed to link location.");
                        }
                    } else {
                        showError("Failed to add customer.");
                    }

                } catch (Exception e) {
                    showError("Invalid input: " + e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
    }


    private void handleEdit() {
        Customer selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a customer to edit.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Customer");

        TextField nameField = new TextField(selected.getName());
        TextField phoneField = new TextField(selected.getPhoneNumber());

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
                selected.setPhoneNumber(phoneField.getText().trim());

                boolean success = CustomerManagement.updateCustomer(selected);
                if (success) {
                    loadCustomers();
                    showInfo("Customer updated successfully.");
                } else {
                    showError("Failed to update customer.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void handleDelete() {
        Customer selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a customer to delete.");
            return;
        }

        boolean success = CustomerManagement.deleteCustomer(selected.getId());
        if (success) {
            loadCustomers();
            showInfo("Customer deleted successfully.");
        } else {
            showError("Failed to delete customer.");
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