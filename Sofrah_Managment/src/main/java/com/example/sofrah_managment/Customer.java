package com.example.sofrah_managment;

public class Customer {
    private int id;
    private String name;
    private String phoneNumber;

    // Constructor with ID (for updates and reading from DB)
    public Customer(int id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phone;
    }
    // Constructor without ID (for adding new customers)
    public Customer(String name, String phone) {
        this.name = name;
        this.phoneNumber = phone;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return id + "- " + name;
    }



}
