package com.example.sofrah_managment;

public class OrderStatus {
    private int orderStatusId;
    private String orderStatusName;


    public OrderStatus(){}

    public OrderStatus(int orderStatusID, String orderStatusName) {
        this.orderStatusId = orderStatusID;
        this.orderStatusName = orderStatusName;
    }


    public int getOrderStatusId() {
        return orderStatusId;
    }

    public void setOrderStatusId(int orderStatusId) {
        this.orderStatusId = orderStatusId;
    }

    public String getOrderStatusName() {
        return orderStatusName;
    }

    public void setOrderStatusName(String orderStatusName) {
        this.orderStatusName = orderStatusName;
    }





}
