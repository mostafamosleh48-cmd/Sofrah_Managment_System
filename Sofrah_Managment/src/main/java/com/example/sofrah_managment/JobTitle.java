package com.example.sofrah_managment;

public class JobTitle {
    private int id;
    private String name;
    private String description;
    private double payRate;

    public JobTitle(int id, String name, String description, double payRate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.payRate = payRate;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPayRate() { return payRate; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPayRate(double payRate) { this.payRate = payRate; }

    public String toString(){
        return id + "- " + name;
    }
}
