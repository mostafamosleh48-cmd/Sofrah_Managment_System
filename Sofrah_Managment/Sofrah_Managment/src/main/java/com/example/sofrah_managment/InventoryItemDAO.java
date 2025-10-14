package Sofrah_Managment.src.main.java.com.example.sofrah_managment;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

public class InventoryItemDAO {


    public List<InventoryItem> getAllInventoryItems() throws SQLException {
        List<InventoryItem> items = new ArrayList<>();
        String sql = "SELECT ID, Name, Stock FROM InventoryItem";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBCon.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                items.add(new InventoryItem(
                        rs.getInt("ID"),
                        rs.getString("Name"),
                        rs.getInt("Stock")
                ));
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
        return items;
    }


    public List<InventoryItem> getLowStockItems(int threshold) throws SQLException {
        List<InventoryItem> items = new ArrayList<>();
        String sql = "SELECT ID, Name, Stock FROM InventoryItem WHERE Stock < ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBCon.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, threshold);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                items.add(new InventoryItem(
                        rs.getInt("ID"),
                        rs.getString("Name"),
                        rs.getInt("Stock")
                ));
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
        return items;
    }
    public void updateStock(int itemId, int quantityChange) throws SQLException {
        String sql = "UPDATE InventoryItem SET Stock = Stock + ? WHERE ID = ?";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, quantityChange);
            pstmt.setInt(2, itemId);
            pstmt.executeUpdate();
        }
    }



    public int addNewInventoryItem(String name, int initialStock) throws SQLException {
        // The SQL query now takes two parameters
        String sql = "INSERT INTO InventoryItem (Name, Stock) VALUES (?, ?)";
        int itemId = -1;
        try (Connection conn = DBCon.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, initialStock); // Set the initial stock parameter
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        itemId = rs.getInt(1);
                    }
                }
            }
            return itemId;
        }
    }


    public InventoryItem findByName(String name) throws SQLException {
        String sql = "SELECT ID, Name, Stock FROM InventoryItem WHERE LOWER(Name) = ?";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name.toLowerCase().trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new InventoryItem(
                            rs.getInt("ID"),
                            rs.getString("Name"),
                            rs.getInt("Stock")
                    );
                }
            }
        }
        return null; // Not found
    }






}