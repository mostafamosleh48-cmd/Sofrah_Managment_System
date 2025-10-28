package com.example.sofrah_managment;

public class MenuItem {
    private int id;
    private String itemName;
    private String description;
    private boolean isAvailable;
    private double price;


    public MenuItem() {}

   // (ID is auto-generated)
    public MenuItem(String itemName, String description, boolean isAvailable, double price) {
        this.itemName = itemName;
        this.description = description;
        this.isAvailable = isAvailable;
        this.price = price;
    }

    // Constructor for retrieving/updating (ID is known)
    public MenuItem(int id, String itemName, String description, boolean isAvailable, double price) {
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
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return itemName;  // or include price/name if desired
    }

}
