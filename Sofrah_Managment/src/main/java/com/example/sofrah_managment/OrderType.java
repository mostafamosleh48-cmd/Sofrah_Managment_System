package com.example.sofrah_managment;

public class OrderType {
    private int orderTypeId;
    private String orderTypeName;

    public OrderType(){}

    public OrderType(int orderTypeId, String orderTypeName) {
        this.orderTypeId = orderTypeId;
        this.orderTypeName = orderTypeName;
    }


    public int getOrderTypeId() {
        return orderTypeId;
    }

    public void setOrderTypeId(int orderTypeId) {
        this.orderTypeId = orderTypeId;
    }

    public String getOrderTypeName() {
        return orderTypeName;
    }

    public void setOrderTypeName(String orderTypeName) {
        this.orderTypeName = orderTypeName;
    }



}
