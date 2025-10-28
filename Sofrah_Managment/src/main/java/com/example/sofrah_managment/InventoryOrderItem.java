package com.example.sofrah_managment;

import java.math.BigDecimal;

public class InventoryOrderItem {

    private int inventoryItemId;
    private int quantity;
    private BigDecimal price;

    public InventoryOrderItem(int inventoryItemId, int quantity, BigDecimal price) {
        this.inventoryItemId = inventoryItemId;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters
    public int getInventoryItemId() { return inventoryItemId; }
    public int getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
}
