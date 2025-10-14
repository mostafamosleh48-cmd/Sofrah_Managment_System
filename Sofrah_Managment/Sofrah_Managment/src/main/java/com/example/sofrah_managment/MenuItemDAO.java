package Sofrah_Managment.src.main.java.com.example.sofrah_managment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class MenuItemDAO {

    public int addMenuItem(MenuItem menuItem, List<MenuIngredient> ingredients) throws SQLException {
        String sqlMenuItem = "INSERT INTO MenuItem (itemName, description, isAvailable, Price) VALUES (?, ?, ?, ?)";
        String sqlMenuIngredient = "INSERT INTO MenuIngredient (MenuItemID, InventoryItemID, quantity) VALUES (?, ?, ?)";
        int menuItemId = -1;

        try (
                Connection conn = DBCon.getConnection();
                PreparedStatement pstmtMenuItem = conn.prepareStatement(sqlMenuItem, Statement.RETURN_GENERATED_KEYS);
        ) {
            pstmtMenuItem.setString(1, menuItem.getItemName());
            pstmtMenuItem.setString(2, menuItem.getDescription());
            pstmtMenuItem.setBoolean(3, menuItem.isAvailable());
            pstmtMenuItem.setBigDecimal(4, menuItem.getPrice());
            pstmtMenuItem.executeUpdate();

            try (ResultSet generatedKeys = pstmtMenuItem.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    menuItemId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating menu item failed, no ID obtained.");
                }
            }

            if (ingredients != null && !ingredients.isEmpty()) {
                for (MenuIngredient ingredient : ingredients) {
                    try (PreparedStatement pstmtIngredient = conn.prepareStatement(sqlMenuIngredient)) {
                        pstmtIngredient.setInt(1, menuItemId);
                        pstmtIngredient.setInt(2, ingredient.getInventoryItemId());
                        pstmtIngredient.setBigDecimal(3, ingredient.getQuantity());
                        pstmtIngredient.executeUpdate();
                    }
                }
            }

            System.out.println("Menu item and ingredients added successfully with ID: " + menuItemId);
            return menuItemId;

        } catch (SQLException e) {
            System.err.println("Error in addMenuItem: " + e.getMessage());
            throw e;
        }
    }

    public void updateMenuItem(MenuItem menuItem, List<MenuIngredient> newIngredients) throws SQLException {
        String sqlUpdateMenuItem = "UPDATE MenuItem SET itemName = ?, description = ?, isAvailable = ?, Price = ? WHERE ID = ?";
        String sqlDeleteIngredients = "DELETE FROM MenuIngredient WHERE MenuItemID = ?";
        String sqlInsertIngredient = "INSERT INTO MenuIngredient (MenuItemID, InventoryItemID, quantity) VALUES (?, ?, ?)";

        try (
                Connection conn = DBCon.getConnection();
                PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdateMenuItem);
                PreparedStatement pstmtDelete = conn.prepareStatement(sqlDeleteIngredients)
        ) {
            // Update item
            pstmtUpdate.setString(1, menuItem.getItemName());
            pstmtUpdate.setString(2, menuItem.getDescription());
            pstmtUpdate.setBoolean(3, menuItem.isAvailable());
            pstmtUpdate.setBigDecimal(4, menuItem.getPrice());
            pstmtUpdate.setInt(5, menuItem.getId());
            pstmtUpdate.executeUpdate();

            // Delete old ingredients
            pstmtDelete.setInt(1, menuItem.getId());
            pstmtDelete.executeUpdate();

            // Insert new ingredients one by one
            if (newIngredients != null && !newIngredients.isEmpty()) {
                for (MenuIngredient ingredient : newIngredients) {
                    try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsertIngredient)) {
                        pstmtInsert.setInt(1, menuItem.getId());
                        pstmtInsert.setInt(2, ingredient.getInventoryItemId());
                        pstmtInsert.setBigDecimal(3, ingredient.getQuantity());
                        pstmtInsert.executeUpdate();
                    }
                }
            }

            System.out.println("Menu item " + menuItem.getId() + " updated successfully.");

        } catch (SQLException e) {
            System.err.println("Error in updateMenuItem: " + e.getMessage());
            throw e;
        }
    }


    public List<MenuItem> getAvailableMenuItems() throws SQLException {
        List<MenuItem> items = new ArrayList<>();

        String sql = "SELECT * FROM MenuItem " +
                "WHERE isAvailable = TRUE AND ID NOT IN (" +
                "  SELECT DISTINCT mi.ID " +
                "  FROM MenuItem mi " +
                "  JOIN MenuIngredient ming ON mi.ID = ming.MenuItemID " +
                "  JOIN InventoryItem ii ON ming.InventoryItemID = ii.ID " +
                "  WHERE ii.Stock < ming.quantity" +
                ")";

        try (Connection conn = DBCon.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(new MenuItem(
                        rs.getInt("ID"),
                        rs.getString("itemName"),
                        rs.getString("description"),
                        true, // isAvailable
                        rs.getBigDecimal("Price")
                ));
            }
        }
        return items;
    }


    public List<MenuItem> getUnavailableMenuItems() throws SQLException {
        List<MenuItem> items = new ArrayList<>();

        String sql = "SELECT * FROM MenuItem " +
                "WHERE isAvailable = FALSE OR ID IN (" +
                "  SELECT DISTINCT mi.ID " +
                "  FROM MenuItem mi " +
                "  JOIN MenuIngredient ming ON mi.ID = ming.MenuItemID " +
                "  JOIN InventoryItem ii ON ming.InventoryItemID = ii.ID " +
                "  WHERE ii.Stock < ming.quantity" +
                ")";

        try (Connection conn = DBCon.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // We can't be sure if it's dynamically or manually unavailable,
                // so we just fetch its current 'isAvailable' flag from the DB.
                items.add(new MenuItem(
                        rs.getInt("ID"),
                        rs.getString("itemName"),
                        rs.getString("description"),
                        rs.getBoolean("isAvailable"),
                        rs.getBigDecimal("Price")
                ));
            }
        }
        return items;
    }




}
