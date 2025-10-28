package com.example.sofrah_managment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerLocation {
    private int customerID;
    private int locationID;

    public CustomerLocation(int customerID, int locationID) {
        this.customerID = customerID;
        this.locationID = locationID;
    }

    public int getCustomerID() { return customerID; }
    public void setCustomerID(int customerID) { this.customerID = customerID; }

    public int getLocationID() { return locationID; }
    public void setLocationID(int locationID) { this.locationID = locationID; }

    // Insert a new CustomerLocation link
    public static boolean linkCustomerToLocation(int customerId, int locationId) {
        String sql = "INSERT INTO CustomerLocation (CustomerID, LocationID) VALUES (?, ?)";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            stmt.setInt(2, locationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error linking customer to location: " + e.getMessage());
            return false;
        }
    }

    // Get locations for a customer (in case multiple)
    public static List<Location> getLocationsForCustomer(int customerId) {
        List<Location> locations = new ArrayList<>();
        String sql = """
            SELECT l.ID, l.City, l.Street
            FROM Location l
            JOIN CustomerLocation cl ON l.ID = cl.LocationID
            WHERE cl.CustomerID = ?
        """;
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                locations.add(new Location(
                        rs.getInt("ID"),
                        rs.getString("City"),
                        rs.getString("Street")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching customer locations: " + e.getMessage());
        }
        return locations;
    }

    // Get the first location for a customer (for use in edit dialog)
    public static Location getPrimaryLocationForCustomer(int customerId) {
        String sql = """
            SELECT l.ID, l.City, l.Street
            FROM Location l
            JOIN CustomerLocation cl ON l.ID = cl.LocationID
            WHERE cl.CustomerID = ?
            LIMIT 1
        """;
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Location(
                        rs.getInt("ID"),
                        rs.getString("City"),
                        rs.getString("Street")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error fetching primary location: " + e.getMessage());
        }
        return null;
    }

    public static boolean isCustomerLinkedToLocation(int customerId, int locationId) {
        String sql = "SELECT 1 FROM CustomerLocation WHERE CustomerID = ? AND LocationID = ?";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            stmt.setInt(2, locationId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();  // true if found
        } catch (SQLException e) {
            System.out.println("Error checking customer-location link: " + e.getMessage());
            return false;
        }
    }

    public static void unlinkCustomerFromAllLocations(int customerId) {
        String sql = "DELETE FROM CustomerLocation WHERE CustomerID = ?";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error unlinking customer from locations: " + e.getMessage());
        }
    }


}
