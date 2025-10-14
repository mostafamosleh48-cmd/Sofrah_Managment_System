package Sofrah_Managment.src.main.java.com.example.sofrah_managment;

import java.math.BigDecimal;

public class MenuItem {
    private int id;
    private String itemName;
    private String description;
    private boolean isAvailable;
    private BigDecimal price;


    public MenuItem() {}

   // (ID is auto-generated)
    public MenuItem(String itemName, String description, boolean isAvailable, BigDecimal price) {
        this.itemName = itemName;
        this.description = description;
        this.isAvailable = isAvailable;
        this.price = price;
    }

    // Constructor for retrieving/updating (ID is known)
    public MenuItem(int id, String itemName, String description, boolean isAvailable, BigDecimal price) {
        this.id = id;
        this.itemName = itemName;
        this.description = description;
        this.isAvailable = isAvailable;
        this.price = price;
    }



    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: '" + itemName + '\'' +
                ", Price: " + price +
                ", Available: " + isAvailable +
                ", Desc: '" + description + '\'';
    }
}
