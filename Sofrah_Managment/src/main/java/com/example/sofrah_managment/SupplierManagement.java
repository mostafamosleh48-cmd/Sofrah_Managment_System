package com.example.sofrah_managment;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SupplierManagement {

    public int addSupplier(Supplier supplier) throws SQLException {
        String sql = "INSERT INTO Supplier (Name, phoneNum) VALUES (?, ?)";
        int supplierId = -1;

        try (
                Connection conn = DBCon.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            pstmt.setString(1, supplier.getName());
            pstmt.setString(2, supplier.getPhoneNum());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    supplierId = generatedKeys.getInt(1);
                    System.out.println("Supplier added successfully with ID: " + supplierId);
                }
            }

            return supplierId;

        } catch (SQLException e) {
            System.err.println("Error in addSupplier: " + e.getMessage());
            throw e;
        }
    }

    public Supplier getSupplierById(int id) throws SQLException {
        String sql = "SELECT ID, Name, phoneNum FROM Supplier WHERE ID = ?";

        try (
                Connection conn = DBCon.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Supplier(
                            rs.getInt("ID"),
                            rs.getString("Name"),
                            rs.getString("phoneNum")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Error in getSupplierById: " + e.getMessage());
            throw e;
        }

        return null;
    }

    // Retrieve the names and contact information of all suppliers
    public static List<Supplier> getAllSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT ID, Name, phoneNum FROM Supplier";

        try (Connection conn = DBCon.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                suppliers.add(new Supplier(
                        rs.getInt("ID"),
                        rs.getString("Name"),
                        rs.getString("phoneNum")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching suppliers: " + e.getMessage());
        }
        return suppliers;
    }

    // Update supplier information
    public static boolean updateSupplier(Supplier supplier) {
        String sql = "UPDATE Supplier SET Name = ?, phoneNum = ? WHERE ID = ?";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getPhoneNum());
            stmt.setInt(3, supplier.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating supplier: " + e.getMessage());
            return false;
        }
    }

    // Delete supplier
    public static boolean deleteSupplier(int id) {
        String sql = "DELETE FROM Supplier WHERE ID = ?";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting supplier: " + e.getMessage());
            return false;
        }
    }

    // Retrieve an order you placed from a supplier on a specific date with the price
    public static class SupplierOrder {
        private int orderId;
        private String supplierName;
        private Date orderDate;
        private double totalPrice;
        private String orderDetails;

        public SupplierOrder(int orderId, String supplierName, Date orderDate, double totalPrice, String orderDetails) {
            this.orderId = orderId;
            this.supplierName = supplierName;
            this.orderDate = orderDate;
            this.totalPrice = totalPrice;
            this.orderDetails = orderDetails;
        }

        // Getters
        public int getOrderId() { return orderId; }
        public String getSupplierName() { return supplierName; }
        public Date getOrderDate() { return orderDate; }
        public double getTotalPrice() { return totalPrice; }
        public String getOrderDetails() { return orderDetails; }
    }

    public static List<SupplierOrder> getSupplierOrdersByDate(String date) {
        List<SupplierOrder> orders = new ArrayList<>();
        String sql = """
            SELECT so.ID as OrderID, s.Name as SupplierName, so.OrderDate, so.TotalPrice, so.OrderDetails
            FROM SupplierOrder so
            JOIN Supplier s ON so.SupplierID = s.ID
            WHERE DATE(so.OrderDate) = ?
            """;

        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, date);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(new SupplierOrder(
                            rs.getInt("OrderID"),
                            rs.getString("SupplierName"),
                            rs.getDate("OrderDate"),
                            rs.getDouble("TotalPrice"),
                            rs.getString("OrderDetails")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching supplier orders by date: " + e.getMessage());
        }
        return orders;
    }

    // Retrieve the list of suppliers who provide a specific ingredient
    public static List<Supplier> getSuppliersByIngredient(String ingredientName) {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = """
            SELECT DISTINCT s.ID, s.Name, s.phoneNum
            FROM Supplier s
            JOIN SupplierIngredient si ON s.ID = si.SupplierID
            JOIN Ingredient i ON si.IngredientID = i.ID
            WHERE i.Name = ?
            """;

        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ingredientName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    suppliers.add(new Supplier(
                            rs.getInt("ID"),
                            rs.getString("Name"),
                            rs.getString("phoneNum")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching suppliers by ingredient: " + e.getMessage());
        }
        return suppliers;
    }


    public static boolean createSupplierOrder(int supplierId, LocalDate orderDate,
                                              double totalPrice, String orderDetails,
                                              List<SupplierUI.OrderItem> orderItems) {

        Connection conn = null;
        try {
            conn = DBCon.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Insert main order record
            String orderSql = "INSERT INTO SupplierOrder (SupplierID, OrderDate, TotalPrice, OrderDetails) VALUES (?, ?, ?, ?)";
            int orderId = -1;

            try (PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                orderStmt.setInt(1, supplierId);
                orderStmt.setDate(2, Date.valueOf(orderDate));
                orderStmt.setDouble(3, totalPrice);
                orderStmt.setString(4, orderDetails);

                orderStmt.executeUpdate();

                // Get generated order ID
                try (ResultSet generatedKeys = orderStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        orderId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Failed to get generated order ID");
                    }
                }
            }

            // Insert order items (if you have a SupplierOrderItem table)
            if (!orderItems.isEmpty()) {
                String itemSql = "INSERT INTO SupplierOrderItem (OrderID, ComponentName, Quantity, UnitPrice, TotalPrice) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement itemStmt = conn.prepareStatement(itemSql)) {
                    for (SupplierUI.OrderItem item : orderItems) {
                        itemStmt.setInt(1, orderId);
                        itemStmt.setString(2, item.getComponentName());
                        itemStmt.setDouble(3, item.getQuantity());
                        itemStmt.setDouble(4, item.getUnitPrice());
                        itemStmt.setDouble(5, item.getQuantity() * item.getUnitPrice());
                        itemStmt.addBatch();
                    }
                    itemStmt.executeBatch();
                }
            }

            conn.commit(); // Commit transaction
            System.out.println("Supplier order created successfully with ID: " + orderId);
            return true;

        } catch (SQLException e) {
            System.err.println("Error creating supplier order: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }


    private static boolean isValidDateFormat(String date) {
        try {
            LocalDate.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}