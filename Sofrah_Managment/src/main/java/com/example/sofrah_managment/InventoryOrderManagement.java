package com.example.sofrah_managment;

import java.sql.*;
import java.util.List;

public class InventoryOrderManagement {

    public int createInventoryOrder(int supplierId, List<InventoryOrderItem> items) throws SQLException {
        String sqlOrder = "INSERT INTO InventoryOrder (SupplierID, OrderDate) VALUES (?, NOW())";
        String sqlOrderItem = "INSERT INTO InventoryOrderItem (InventoryOrderID, InventoryItemID, Price, Stock) VALUES (?, ?, ?, ?)";
        int orderId = -1;

        try (
                Connection conn = DBCon.getConnection();
                PreparedStatement psOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)
        ) {
            psOrder.setInt(1, supplierId);
            psOrder.executeUpdate();

            try (ResultSet generatedKeys = psOrder.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    orderId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating inventory order failed, no ID obtained.");
                }
            }

            if (items != null && !items.isEmpty()) {
                for (InventoryOrderItem item : items) {
                    try (PreparedStatement psItem = conn.prepareStatement(sqlOrderItem)) {
                        psItem.setInt(1, orderId);
                        psItem.setInt(2, item.getInventoryItemId());
                        psItem.setBigDecimal(3, item.getPrice());
                        psItem.setInt(4, item.getQuantity());
                        psItem.executeUpdate();
                    }
                }
            }

            System.out.println("Successfully created inventory order with ID: " + orderId);
            return orderId;

        } catch (SQLException e) {
            System.err.println("Error in createInventoryOrder: " + e.getMessage());
            throw e;
        }
    }

    public void receiveInventoryOrder(int orderId) throws SQLException {
        String getItemsSQL = "SELECT InventoryItemID, Stock FROM InventoryOrderItem WHERE InventoryOrderID = ?";
        String updateStockSQL = "UPDATE InventoryItem SET Stock = Stock + ? WHERE ID = ?";

        try (
                Connection conn = DBCon.getConnection();
                PreparedStatement psGetItems = conn.prepareStatement(getItemsSQL)
        ) {
            psGetItems.setInt(1, orderId);
            try (ResultSet rs = psGetItems.executeQuery()) {
                int itemsUpdated = 0;

                while (rs.next()) {
                    int itemId = rs.getInt("InventoryItemID");
                    int quantity = rs.getInt("Stock");

                    try (PreparedStatement psUpdate = conn.prepareStatement(updateStockSQL)) {
                        psUpdate.setInt(1, quantity);
                        psUpdate.setInt(2, itemId);
                        psUpdate.executeUpdate();
                        itemsUpdated++;
                    }
                }

                if (itemsUpdated > 0) {
                    System.out.println("Successfully updated stock for " + itemsUpdated + " items from order ID: " + orderId);
                } else {
                    System.out.println("No items found for order ID: " + orderId + ". No stock updated.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error in receiveInventoryOrder: " + e.getMessage());
            throw e;
        }
    }
}
