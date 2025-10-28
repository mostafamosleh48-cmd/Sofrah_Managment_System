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

public class EmployeeUI {

    private TableView<Employee> tableView;
    private ObservableList<Employee> employeeList;

    public VBox getView() {
        Label title = new Label("Employee Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        tableView = new TableView<>();
        setupTable();
        loadAllEmployees();

        // Top Filter Buttons
        Button btnAll = new Button("All Employees");
        Button btnByJob = new Button("By Job Title");
        Button btnShifts = new Button("Upcoming Shifts");
        Button btnSalary = new Button("Work Hours / Salary");
        Button btnDaysOff = new Button("Days Off");

        btnAll.setOnAction(e -> loadAllEmployees());
        btnByJob.setOnAction(e -> handleByJobTitle());
        btnShifts.setOnAction(e -> handleUpcomingShifts());
        btnSalary.setOnAction(e -> handleSalaries());
        btnDaysOff.setOnAction(e -> handleDaysOff());

        HBox topButtons = new HBox(10, btnAll, btnByJob, btnShifts, btnDaysOff, btnSalary);
        topButtons.setPadding(new Insets(10));
        topButtons.setAlignment(Pos.CENTER_LEFT);

        // Bottom Action Buttons
        Button btnAdd = new Button("Add");
        Button btnEdit = new Button("Edit");
        Button btnDelete = new Button("Delete");
        Button btnRefresh = new Button("Refresh");

        btnAdd.setOnAction(e -> handleAdd());
        btnEdit.setOnAction(e -> handleEdit());
        btnDelete.setOnAction(e -> handleDelete());
        btnRefresh.setOnAction(e -> loadAllEmployees());

        HBox bottomButtons = new HBox(10, btnAdd, btnEdit, btnDelete, btnRefresh);
        bottomButtons.setPadding(new Insets(10));
        bottomButtons.setAlignment(Pos.CENTER);

        VBox root = new VBox(10, title, topButtons, tableView, bottomButtons);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f4f4f4;");
        return root;
    }

    private void setupTable() {
        TableColumn<Employee, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Employee, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Employee, Integer> jobCol = new TableColumn<>("Job Title ID");
        jobCol.setCellValueFactory(new PropertyValueFactory<>("jobTitleId"));

        TableColumn<Employee, LocalDate> dobCol = new TableColumn<>("Date of Birth");
        dobCol.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));

        tableView.getColumns().addAll(idCol, nameCol, jobCol, dobCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadAllEmployees() {
        List<Employee> employees = EmployeeManagement.getAllEmployees();
        employeeList = FXCollections.observableArrayList(employees);
        tableView.setItems(employeeList);
    }

    private void handleAdd() {
        Dialog<Employee> dialog = new Dialog<>();
        dialog.setTitle("Add Employee");

        TextField nameField = new TextField();
        TextField jobField = new TextField();
        DatePicker dobPicker = new DatePicker();

        VBox content = new VBox(10,
                new Label("Name:"), nameField,
                new Label("Job Title ID:"), jobField,
                new Label("Date of Birth:"), dobPicker
        );
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    Employee emp = new Employee(0,
                            Integer.parseInt(jobField.getText()),
                            dobPicker.getValue(),
                            nameField.getText());
                    boolean success = EmployeeManagement.addEmployee(emp);
                    if (success) {
                        loadAllEmployees();
                        showInfo("Employee added.");
                    } else {
                        showError("Failed to add.");
                    }
                } catch (Exception e) {
                    showError("Invalid input.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void handleEdit() {
        Employee selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select employee first.");
            return;
        }

        Dialog<Employee> dialog = new Dialog<>();
        dialog.setTitle("Edit Employee");

        TextField nameField = new TextField(selected.getName());
        TextField jobField = new TextField(String.valueOf(selected.getJobTitleId()));
        DatePicker dobPicker = new DatePicker(selected.getDateOfBirth());

        VBox content = new VBox(10,
                new Label("Name:"), nameField,
                new Label("Job Title ID:"), jobField,
                new Label("Date of Birth:"), dobPicker
        );
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    selected.setName(nameField.getText());
                    selected.setJobTitleId(Integer.parseInt(jobField.getText()));
                    selected.setDateOfBirth(dobPicker.getValue());
                    boolean success = EmployeeManagement.updateEmployee(selected);
                    if (success) {
                        loadAllEmployees();
                        showInfo("Employee updated.");
                    } else {
                        showError("Update failed.");
                    }
                } catch (Exception e) {
                    showError("Invalid input.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void handleDelete() {
        Employee selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select employee to delete.");
            return;
        }

        boolean success = EmployeeManagement.deleteEmployee(selected.getId());
        if (success) {
            loadAllEmployees();
            showInfo("Deleted.");
        } else {
            showError("Delete failed.");
        }
    }

    private void handleByJobTitle() {
        TextInputDialog input = new TextInputDialog();
        input.setHeaderText("Enter Job Title Name:");
        input.showAndWait().ifPresent(title -> {
            List<Employee> filtered = EmployeeManagement.getEmployeesByJobTitle(title);
            tableView.setItems(FXCollections.observableArrayList(filtered));
        });
    }

    private void handleUpcomingShifts() {
        List<Employee> list = EmployeeManagement.getEmployeesWithUpcomingShifts();
        tableView.setItems(FXCollections.observableArrayList(list));
    }

    private void handleSalaries() {
        List<String> result = EmployeeManagement.getEmployeesWorkHoursAndSalaries();

        if (result == null || result.isEmpty()) {
            showInfo("No salary data available.\nPlease make sure there are valid shifts with check-in and check-out times marked as 'worked'.");
            return;
        }

        StringBuilder sb = new StringBuilder("Employee Work Hours and Salaries:\n\n");
        for (String line : result) {
            sb.append(line).append("\n");
        }

        showInfo(sb.toString());
    }

    private void handleDaysOff() {
        List<String> result = EmployeeManagement.getEmployeeDaysOff();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, String.join("\n", result));
        alert.setTitle("Employee Days Off");
        alert.setHeaderText("Absences Report");
        alert.setResizable(true);
        alert.showAndWait();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
