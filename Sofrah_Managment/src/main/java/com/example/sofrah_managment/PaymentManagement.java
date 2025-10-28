package com.example.sofrah_managment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentManagement {

    // Add a payment
    public static boolean addPayment(Payment payment) {
        String sql = "INSERT INTO Payment (OrderID, Amount, paymentName) VALUES (?, ?, ?)";

        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, payment.getOrderId());
            stmt.setDouble(2, payment.getAmount());
            stmt.setString(3, payment.getPaymentName());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error adding payment: " + e.getMessage());
            return false;
        }
    }


    public static boolean updatePayment(Payment payment) {
        String sql = "UPDATE Payment SET OrderID = ?, Amount = ?, PaymentName = ? WHERE ID = ?";

        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, payment.getOrderId());
            stmt.setDouble(2, payment.getAmount());
            stmt.setString(3, payment.getPaymentName());
            stmt.setInt(4, payment.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating payment: " + e.getMessage());
            return false;
        }
    }


    // Delete a payment by ID
    public static boolean deletePayment(int id) {
        String sql = "DELETE FROM payment WHERE id = ?";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting payment: " + e.getMessage());
            return false;
        }
    }

    // Get all payments
    public static List<Payment> getAllPayments() {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM Payment";

        try (Connection conn = DBCon.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Payment(
                        rs.getInt("ID"),
                        rs.getInt("OrderID"),
                        rs.getDouble("Amount"),
                        rs.getString("paymentName")  // <-- include this
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching payments: " + e.getMessage());
        }

        return list;
    }



    // Get order ID list for dropdowns if needed
    public static List<Integer> getOrderIds() {
        List<Integer> orderIds = new ArrayList<>();
        String sql = "SELECT id FROM orders";

        try (Connection conn = DBCon.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                orderIds.add(rs.getInt("id"));
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving order IDs: " + e.getMessage());
        }

        return orderIds;
    }
}
