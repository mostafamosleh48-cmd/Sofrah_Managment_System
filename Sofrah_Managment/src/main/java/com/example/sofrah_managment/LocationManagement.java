package com.example.sofrah_managment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocationManagement {
    public static List<Location> getAllLocations() {
        List<Location> list = new ArrayList<>();
        String sql = "SELECT * FROM Location";

        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Location location = new Location(
                        rs.getInt("ID"),
                        rs.getString("City"),
                        rs.getString("Street")
                );
                list.add(location);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching locations: " + e.getMessage());
        }

        return list;
    }


    //  Total number of orders by location

    public static List<String> getOrderCountsByLocation() {
        List<String> results = new ArrayList<>();
        String sql = "SELECT l.City, l.Street, COUNT(o.ID) AS orderCount " +
                "FROM Location l " +
                "JOIN CustomerLocation cl ON l.ID = cl.LocationID " +
                "JOIN Orders o ON cl.CustomerID = o.customerID " +
                "GROUP BY l.ID, l.City, l.Street";

        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String entry = rs.getString("City") + ", " + rs.getString("Street") + " - Orders: " + rs.getInt("orderCount");
                results.add(entry);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving order counts by location: " + e.getMessage());
        }
        return results;
    }

    public static int addOrGetLocationId(Location location) {
        String selectSql = "SELECT ID FROM Location WHERE City = ? AND Street = ?";
        String insertSql = "INSERT INTO Location (City, Street) VALUES (?, ?)";

        try (Connection conn = DBCon.getConnection()) {
            // Check if location exists
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setString(1, location.getCity());
                selectStmt.setString(2, location.getStreet());
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) return rs.getInt("ID");
            }

            // Insert new location
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, location.getCity());
                insertStmt.setString(2, location.getStreet());
                insertStmt.executeUpdate();
                ResultSet rs = insertStmt.getGeneratedKeys();
                if (rs.next()) return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Error adding/getting location: " + e.getMessage());
        }
        return -1;
    }



}
