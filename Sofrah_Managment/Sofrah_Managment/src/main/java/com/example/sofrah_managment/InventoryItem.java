package Sofrah_Managment.src.main.java.com.example.sofrah_managment;

public class InventoryItem {
    private int id;
    private String name;
    private int stock;

    // Constructors
    public InventoryItem() {
    }

    public InventoryItem(int id, String name, int stock) {
        this.id = id;
        this.name = name;
        this.stock = stock;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}