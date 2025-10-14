package Sofrah_Managment.src.main.java.com.example.sofrah_managment;

import java.math.BigDecimal;

public class MenuIngredient {
    private int menuItemId;
    private int inventoryItemId;
    private BigDecimal quantity;

    public MenuIngredient(int inventoryItemId, BigDecimal quantity) {
        this.inventoryItemId = inventoryItemId;
        this.quantity = quantity;
    }


    public int getInventoryItemId() { return inventoryItemId; }
    public BigDecimal getQuantity() { return quantity; }


    public void setMenuItemId(int menuItemId) { this.menuItemId = menuItemId; }
    public int getMenuItemId() { return menuItemId; }
}