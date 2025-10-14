//package com.example.sofrah_managment;
//
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class DBOperations {
//
//    // ===== Customer Methods =====
//
//    public static boolean addCustomer(int id, String name, String phone) {
//        String sql = "INSERT INTO Customer (customer_id, name, phone) VALUES (?, ?, ?)";
//        try (Connection conn = DBCon.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, id);
//            stmt.setString(2, name);
//            stmt.setString(3, phone);
//            stmt.executeUpdate();
//            return true;
//        } catch (SQLException e) {
//            System.out.println("Error inserting customer: " + e.getMessage());
//            return false;
//        }
//    }
//
//    public static boolean updateCustomer(int id, String name, String phone) {
//        String sql = "UPDATE Customer SET name = ?, phone = ? WHERE customer_id = ?";
//        try (Connection conn = DBCon.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setString(1, name);
//            stmt.setString(2, phone);
//            stmt.setInt(3, id);
//            return stmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            System.out.println("Error updating customer: " + e.getMessage());
//            return false;
//        }
//    }
//
//    public static boolean deleteCustomer(int id) {
//        String sql = "DELETE FROM Customer WHERE customer_id = ?";
//        try (Connection conn = DBCon.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, id);
//            return stmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            System.out.println("Error deleting customer: " + e.getMessage());
//            return false;
//        }
//    }
//
//    public static String getCustomer(int id) {
//        String sql = "SELECT name, phone FROM Customer WHERE customer_id = ?";
//        try (Connection conn = DBCon.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, id);
//            ResultSet rs = stmt.executeQuery();
//            if (rs.next()) {
//                return "Name: " + rs.getString("name") + ", Phone: " + rs.getString("phone");
//            } else {
//                return "Customer not found.";
//            }
//        } catch (SQLException e) {
//            System.out.println("Error reading customer: " + e.getMessage());
//            return "Error retrieving customer.";
//        }
//    }
//
//    public static List<Customer> getAllCustomers() {
//        List<Customer> customers = new ArrayList<>();
//        String sql = "SELECT * FROM Customer";
//        try (Connection conn = DBCon.getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//            while (rs.next()) {
//                customers.add(new Customer(
//                        rs.getInt("customer_id"),
//                        rs.getString("name"),
//                        rs.getString("phone")
//                ));
//            }
//        } catch (SQLException e) {
//            System.out.println("Error fetching all customers: " + e.getMessage());
//        }
//        return customers;
//    }
//
//    // ===== Order Methods =====
//
//
//    public static boolean addOrder(int orderId, int customerId, String orderDate, double amount) {
//        String sql = "INSERT INTO Orders (order_id, customer_id, order_date, amount) VALUES (?, ?, ?, ?)";
//        try (Connection conn = DBCon.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, orderId);
//            stmt.setInt(2, customerId);
//            stmt.setString(3, orderDate);
//            stmt.setDouble(4, amount);
//            stmt.executeUpdate();
//            return true;
//        } catch (SQLException e) {
//            System.out.println("Error adding order: " + e.getMessage());
//            return false;
//        }
//    }
//
//    public static boolean deleteOrder(int orderId) {
//        String sql = "DELETE FROM Orders WHERE order_id = ?";
//        try (Connection conn = DBCon.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, orderId);
//            return stmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            System.out.println("Error deleting order: " + e.getMessage());
//            return false;
//        }
//    }
//
//    public static List<Order> getAllOrders() {
//        List<Order> orders = new ArrayList<>();
//        String sql = "SELECT * FROM Orders";
//        try (Connection conn = DBCon.getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//            while (rs.next()) {
//                orders.add(new Order(
//                        rs.getInt("order_id"),
//                        rs.getInt("customer_id"),
//                        rs.getString("order_date"),
//                        rs.getDouble("amount")
//                ));
//            }
//        } catch (SQLException e) {
//            System.out.println("Error fetching orders: " + e.getMessage());
//        }
//        return orders;
//    }
//}
