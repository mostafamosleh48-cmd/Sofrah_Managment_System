package com.example.sofrah_managment;

public class Payment {
    private int id;
    private int orderId;
    private double amount;
    private String paymentName;


    public Payment(int id, int orderId, double amount, String paymentName) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentName = paymentName;
    }

    public int getId() { return id; }
    public int getOrderId() { return orderId; }
    public double getAmount() { return amount; }

    public void setId(int id) { this.id = id; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }


}
