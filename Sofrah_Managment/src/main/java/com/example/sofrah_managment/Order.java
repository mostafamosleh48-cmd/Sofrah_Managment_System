package com.example.sofrah_managment;

import java.time.LocalDateTime;

public class Order {
    private int id;
    private int customerId;
    private int employeeId;
    private int orderStatusId;
    private int orderTypeId;
    private LocalDateTime date;

    public Order(int id, int customerId, int employeeId, int orderStatusId, int orderTypeId, LocalDateTime date) {
        this.id = id;
        this.customerId = customerId;
        this.employeeId = employeeId;
        this.orderStatusId = orderStatusId;
        this.orderTypeId = orderTypeId;
        this.date = date;
    }

    public Order(int customerId, int employeeId, int orderStatusId, int orderTypeId, LocalDateTime date) {
        this.customerId = customerId;
        this.employeeId = employeeId;
        this.orderStatusId = orderStatusId;
        this.orderTypeId = orderTypeId;
        this.date = date;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public int getOrderStatusId() {
        return orderStatusId;
    }

    public int getOrderTypeId() {
        return orderTypeId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public void setOrderStatusId(int orderStatusId) {
        this.orderStatusId = orderStatusId;
    }

    public void setOrderTypeId(int orderTypeId) {
        this.orderTypeId = orderTypeId;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
