package com.example.sofrah_managment;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ShiftsUI {

    private BorderPane mainLayout;
    private TableView<Shift> tableView;
    private ObservableList<Shift> shiftList;

    public ShiftsUI() {
        mainLayout = new BorderPane();
        shiftList = FXCollections.observableArrayList();

        Label titleLabel = new Label("Shifts");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(10));

        tableView = new TableView<>();
        setupTableColumns();
        refreshTable();

        Button refreshBtn = new Button("Refresh");
        Button addBtn = new Button("Add Shift");
        Button editBtn = new Button("Edit Shift");

        refreshBtn.setOnAction(e -> refreshTable());
        addBtn.setOnAction(e -> handleAddShift());
        editBtn.setOnAction(e -> handleEditShift());

        HBox buttonBox = new HBox(10, addBtn, editBtn, refreshBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));

        VBox content = new VBox(10, titleBox, tableView, buttonBox);
        content.setPadding(new Insets(10));

        mainLayout.setCenter(content);
    }

    private void setupTableColumns() {
        TableColumn<Shift, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Shift, Integer> employeeIdCol = new TableColumn<>("Employee ID");
        employeeIdCol.setCellValueFactory(new PropertyValueFactory<>("employeeId"));

        TableColumn<Shift, String> checkInCol = new TableColumn<>("Check-In");
        checkInCol.setCellValueFactory(new PropertyValueFactory<>("checkInFormatted"));

        TableColumn<Shift, String> checkOutCol = new TableColumn<>("Check-Out");
        checkOutCol.setCellValueFactory(new PropertyValueFactory<>("checkOutFormatted"));

        TableColumn<Shift, String> expectedInCol = new TableColumn<>("Expected In");
        expectedInCol.setCellValueFactory(new PropertyValueFactory<>("expectedCheckInFormatted"));

        TableColumn<Shift, Boolean> workedCol = new TableColumn<>("Worked");
        workedCol.setCellValueFactory(new PropertyValueFactory<>("hasWorked"));

        tableView.getColumns().addAll(idCol, employeeIdCol, checkInCol, checkOutCol, expectedInCol, workedCol);
    }

    private void refreshTable() {
        shiftList.clear();
        List<Shift> shifts = ShiftManagement.getAllShifts();
        if (shifts != null) {
            shiftList.addAll(shifts);
        }
        tableView.setItems(shiftList);
    }

    private void handleAddShift() {
        Dialog<Shift> dialog = new Dialog<>();
        dialog.setTitle("Add Shift");

        ComboBox<Employee> employeeCombo = new ComboBox<>(FXCollections.observableArrayList(EmployeeManagement.getAllEmployees()));
        DatePicker datePicker = new DatePicker();
        TextField inField = new TextField("09:00");
        TextField outField = new TextField("17:00");

        VBox box = new VBox(10,
                new Label("Employee:"), employeeCombo,
                new Label("Expected Date:"), datePicker,
                new Label("Expected Check-In (HH:mm):"), inField,
                new Label("Expected Check-Out (HH:mm):"), outField
        );
        box.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    Employee emp = employeeCombo.getValue();
                    if (emp == null || datePicker.getValue() == null || inField.getText().isEmpty() || outField.getText().isEmpty()) {
                        throw new IllegalArgumentException("All fields are required.");
                    }

                    LocalDate date = datePicker.getValue();
                    LocalDateTime expectedCheckIn = LocalDateTime.of(date, java.time.LocalTime.parse(inField.getText()));
                    LocalDateTime expectedCheckOut = LocalDateTime.of(date, java.time.LocalTime.parse(outField.getText()));

                    // عند الإضافة، ما اشتغل لسه => false
                    return new Shift(0, emp.getId(), null, null, expectedCheckIn, expectedCheckOut, false);

                } catch (Exception e) {
                    showError("Input error: " + e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(shift -> {
            boolean success = ShiftManagement.addShift(shift);
            if (success) {
                refreshTable();
                showInfo("Shift added successfully.");
            } else {
                showError("Failed to add shift.");
            }
        });
    }

    private void handleEditShift() {
        Shift selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a shift to edit.");
            return;
        }

        Dialog<Shift> dialog = new Dialog<>();
        dialog.setTitle("Edit Shift");

        DatePicker datePicker = new DatePicker(selected.getExpectedCheckIn().toLocalDate());
        TextField checkInField = new TextField();
        TextField checkOutField = new TextField();
        CheckBox workedCheck = new CheckBox("Has Worked");
        workedCheck.setSelected(selected.isHasWorked());

        if (selected.getCheckIn() != null)
            checkInField.setText(selected.getCheckIn().toLocalTime().toString().substring(0, 5)); // "HH:mm"
        if (selected.getCheckOut() != null)
            checkOutField.setText(selected.getCheckOut().toLocalTime().toString().substring(0, 5));

        VBox content = new VBox(10,
                new Label("Expected Date:"), datePicker,
                new Label("Actual Check-In (HH:mm):"), checkInField,
                new Label("Actual Check-Out (HH:mm):"), checkOutField,
                workedCheck
        );
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    LocalDate date = datePicker.getValue();
                    LocalDateTime checkIn = LocalDateTime.of(date, java.time.LocalTime.parse(checkInField.getText()));
                    LocalDateTime checkOut = LocalDateTime.of(date, java.time.LocalTime.parse(checkOutField.getText()));

                    selected.setCheckIn(checkIn);
                    selected.setCheckOut(checkOut);
                    selected.setHasWorked(workedCheck.isSelected());

                    return selected;
                } catch (Exception e) {
                    showError("Invalid time format. Use HH:mm");
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updated -> {
            boolean success = ShiftManagement.updateShift(updated);
            if (success) {
                refreshTable();
                showInfo("Shift updated.");
            } else {
                showError("Update failed.");
            }
        });
    }


    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }

    public BorderPane getView() {
        return mainLayout;
    }
}
