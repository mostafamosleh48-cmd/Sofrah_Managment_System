package com.example.sofrah_managment;

public class OrderType {
    private int id;
    private String name;

    public OrderType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }

    public String toString() {
        return id + " - " + name; // or just return name;
    }
}