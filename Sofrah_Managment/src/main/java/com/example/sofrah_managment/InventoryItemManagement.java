package com.example.sofrah_managment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryItemManagement {

    // Get all inventory items
    public static List<InventoryItem> getAllInventoryItems() {
        List<InventoryItem> items = new ArrayList<>();
        String sql = "SELECT ID, Name, Stock FROM InventoryItem";

        try (Connection conn = DBCon.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(new InventoryItem(
                        rs.getInt("ID"),
                        rs.getString("Name"),
                        rs.getInt("Stock")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving inventory items: " + e.getMessage());
        }

        return items;
    }

    // Get low stock inventory items
    public static List<InventoryItem> getLowStockItems(int threshold) {
        List<InventoryItem> items = new ArrayList<>();
        String sql = "SELECT ID, Name, Stock FROM InventoryItem WHERE Stock < ?";

        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, threshold);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                items.add(new InventoryItem(
                        rs.getInt("ID"),
                        rs.getString("Name"),
                        rs.getInt("Stock")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving low stock items: " + e.getMessage());
        }

        return items;
    }

    // Update stock of an inventory item
    public static boolean updateStock(int itemId, int quantityChange) {
        String sql = "UPDATE InventoryItem SET Stock = Stock + ? WHERE ID = ?";

        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantityChange);
            stmt.setInt(2, itemId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating stock: " + e.getMessage());
            return false;
        }
    }

    // Add a new inventory item
    public static int addNewInventoryItem(String name, int initialStock) {
        String sql = "INSERT INTO InventoryItem (Name, Stock) VALUES (?, ?)";
        int itemId = -1;

        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            stmt.setInt(2, initialStock);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        itemId = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error adding inventory item: " + e.getMessage());
        }

        return itemId;
    }

    // Find inventory item by name (case-insensitive)
    public static InventoryItem findByName(String name) {
        String sql = "SELECT ID, Name, Stock FROM InventoryItem WHERE LOWER(Name) = ?";

        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name.toLowerCase().trim());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new InventoryItem(
                        rs.getInt("ID"),
                        rs.getString("Name"),
                        rs.getInt("Stock")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error finding inventory item by name: " + e.getMessage());
        }

        return null;
    }

}
