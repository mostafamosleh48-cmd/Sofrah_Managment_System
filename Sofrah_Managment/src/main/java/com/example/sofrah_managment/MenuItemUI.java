package com.example.sofrah_managment;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class MenuItemUI {
    private TableView<MenuItem> tableView;
    private ObservableList<MenuItem> menuItems;
    private MenuItemManagement menuItemManagement;
    private VBox root;

    public MenuItemUI() {
        menuItemManagement = new MenuItemManagement();
        root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f4f4f4;");

        Label title = new Label("Menu Item Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        tableView = new TableView<>();
        setupTable();

        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.CENTER);

        Button btnAvailable = new Button("Available Items");
        Button btnUnavailable = new Button("Unavailable Items");
        Button btnAdd = new Button("Add Menu Item");
        Button btnEdit = new Button("Edit Menu Item");

        btnAvailable.setOnAction(e -> loadMenuItems(true));
        btnUnavailable.setOnAction(e -> loadMenuItems(false));
        btnAdd.setOnAction(e -> openMenuItemForm(null));
        btnEdit.setOnAction(e -> {
            MenuItem selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                openMenuItemForm(selected);
            } else {
                showError("Please select a menu item to edit.");
            }
        });

        buttonBar.getChildren().addAll(btnAvailable, btnUnavailable, btnAdd, btnEdit);
        root.getChildren().addAll(title, tableView, buttonBar);

        // Load default view
        loadMenuItems(true);
    }

    private void setupTable() {
        TableColumn<MenuItem, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<MenuItem, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));

        TableColumn<MenuItem, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<MenuItem, Boolean> availCol = new TableColumn<>("Available");
        availCol.setCellValueFactory(new PropertyValueFactory<>("available"));

        TableColumn<MenuItem, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        tableView.getColumns().addAll(idCol, nameCol, descCol, availCol, priceCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadMenuItems(boolean available) {
        try {
            List<MenuItem> items = available
                    ? menuItemManagement.getAvailableMenuItems()
                    : menuItemManagement.getUnavailableMenuItems();
            menuItems = FXCollections.observableArrayList(items);
            tableView.setItems(menuItems);
        } catch (SQLException e) {
            showError("Failed to load menu items.");
        }
    }

    private void openMenuItemForm(MenuItem itemToEdit) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(itemToEdit == null ? "Add Menu Item" : "Edit Menu Item");

        TextField nameField = new TextField();
        TextArea descField = new TextArea();
        CheckBox availableBox = new CheckBox("Available");
        TextField priceField = new TextField();

        if (itemToEdit != null) {
            nameField.setText(itemToEdit.getItemName());
            descField.setText(itemToEdit.getDescription());
            availableBox.setSelected(itemToEdit.isAvailable());
            priceField.setText(String.valueOf(itemToEdit.getPrice()));
        }

        VBox form = new VBox(10,
                new Label("Item Name:"), nameField,
                new Label("Description:"), descField,
                availableBox,
                new Label("Price:"), priceField
        );
        form.setPadding(new Insets(15));

        Button submit = new Button(itemToEdit == null ? "Add" : "Update");
        submit.setOnAction(e -> {
            try {
                String name = nameField.getText();
                String desc = descField.getText();
                boolean available = availableBox.isSelected();
                double price = Double.parseDouble(priceField.getText());

                MenuItem newItem = itemToEdit == null
                        ? new MenuItem(name, desc, available, price)
                        : new MenuItem(itemToEdit.getId(), name, desc, available, price);

                if (itemToEdit == null) {
                    menuItemManagement.addMenuItem(newItem, null);
                } else {
                    menuItemManagement.updateMenuItem(newItem, null);
                }
                loadMenuItems(true);
                dialog.close();
            } catch (Exception ex) {
                showError("Invalid input: " + ex.getMessage());
            }
        });

        VBox dialogLayout = new VBox(10, form, submit);
        dialogLayout.setPadding(new Insets(20));
        dialog.setScene(new Scene(dialogLayout));
        dialog.showAndWait();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }

    public VBox getView() {
        return root;
    }
}
