package com.example.sofrah_managment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Location {
    private int id;
    private String city;
    private String street;

    public Location(int id, String city, String street) {
        this.id = id;
        this.city = city;
        this.street = street;
    }

    public int getId() { return id; }
    public String getCity() { return city; }
    public String getStreet() { return street; }

    public void setId(int id) { this.id = id; }
    public void setCity(String city) { this.city = city; }
    public void setStreet(String street) { this.street = street; }


    @Override
    public String toString() {
        return city + ", " + street;
    }



}
