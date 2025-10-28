package com.example.sofrah_managment;

public class MenuIngredient {
    private int menuItemId;
    private int inventoryItemId;
    private double quantity;

    public MenuIngredient(int inventoryItemId, double quantity) {
        this.inventoryItemId = inventoryItemId;
        this.quantity = quantity;
    }


    public int getInventoryItemId() { return inventoryItemId; }
    public double getQuantity() { return quantity; }


    public void setMenuItemId(int menuItemId) { this.menuItemId = menuItemId; }
    public int getMenuItemId() { return menuItemId; }
}