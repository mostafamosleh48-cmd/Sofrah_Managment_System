package com.example.sofrah_managment;

public class OrderItem {
    private int id;
    private int menuItemId;
    private int orderId;
    private int quantity;

    public OrderItem(int id, int menuItemId, int orderId, int quantity) {
        this.id = id;
        this.menuItemId = menuItemId;
        this.orderId = orderId;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public int getMenuItemId() { return menuItemId; }
    public int getOrderId() { return orderId; }
    public int getQuantity() { return quantity; }

    public void setId(int id) { this.id = id; }
    public void setMenuItemId(int menuItemId) { this.menuItemId = menuItemId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
