package com.example.sofrah_managment;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainUI extends Application {

    private BorderPane root;

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();

        VBox sideMenu = new VBox(10);
        sideMenu.setPadding(new Insets(20));
        sideMenu.setStyle("-fx-background-color: #2c3e50;");

        Button btnOrders = new Button("🧾 Orders");
        Button btnCustomers = new Button("👤 Customers");
        Button btnSuppliers = new Button("🚚 Suppliers");
        Button btnEmployees = new Button("🔍 Employees");
        Button btnMenuItems = new Button("🍽️ Menu Items");
        Button btnInventory = new Button("🧰 Inventory");
        Button btnPayments = new Button("🧾 Payments");
        Button btnShifts = new Button("🕒 Shifts");
        Button btnJobTitles = new Button("📄 Job Titles");

        // Styling for all buttons
        for (Button btn : new Button[]{btnOrders, btnCustomers, btnSuppliers, btnEmployees, btnMenuItems, btnInventory, btnPayments, btnShifts, btnJobTitles}) {
            btn.setPrefWidth(180);
            btn.setStyle("-fx-background-color: #bdc3c7; -fx-font-size: 12px; -fx-padding: 10px;");
        }

        // Button Actions
        btnOrders.setOnAction(e -> root.setCenter(new OrderUI().getView()));
        btnCustomers.setOnAction(e -> root.setCenter(new CustomerUI().getView()));
        btnSuppliers.setOnAction(e -> root.setCenter(new SupplierUI().getView()));
        btnEmployees.setOnAction(e -> root.setCenter(new EmployeeUI().getView()));
        btnMenuItems.setOnAction(e -> root.setCenter(new MenuItemUI().getView()));
//        btnMenuItemOrders.setOnAction(e -> root.setCenter(new MenuItemOrderUI().getView()));
        btnInventory.setOnAction(e -> root.setCenter(new InventoryUI().getView()));
        btnPayments.setOnAction(e -> root.setCenter(new PaymentUI().getView()));
        btnShifts.setOnAction(e -> root.setCenter(new ShiftsUI().getView()));
        btnJobTitles.setOnAction(e -> root.setCenter(new JobTitleUI().getView()));

        sideMenu.getChildren().addAll(
                btnOrders, btnCustomers, btnSuppliers, btnEmployees, btnMenuItems, btnInventory, btnPayments, btnShifts, btnJobTitles
        );

        root.setLeft(sideMenu);

        // Set a default view on startup
        root.setCenter(new OrderUI().getView());

        Scene scene = new Scene(root, 1200, 700); // Increased size to accommodate the new UI
        primaryStage.setTitle("Sofrah Kitchen Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}