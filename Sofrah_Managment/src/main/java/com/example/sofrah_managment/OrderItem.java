package com.example.sofrah_managment;

public class OrderItem {
    private int orderItemID;
    private int quantity;

    public OrderItem(){}


    public OrderItem(int quantity, int orderItemID) {
        this.quantity = quantity;
        this.orderItemID = orderItemID;
    }


    public int getOrderItemID() {
        return orderItemID;
    }

    public void setOrderItemID(int orderItemID) {
        this.orderItemID = orderItemID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
