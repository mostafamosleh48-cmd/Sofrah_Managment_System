package com.example.sofrah_managment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerManagement {

    public static boolean addCustomer(Customer customer) {
        String sql = "INSERT INTO customer (Name, PhoneNumber) VALUES (?, ?)";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getPhoneNumber());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding customer: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateCustomer(Customer customer) {
        String sql = "UPDATE customer SET Name = ?, PhoneNumber = ? WHERE ID = ?";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getPhoneNumber());
            stmt.setInt(3, customer.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating customer: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteCustomer(int id) {
        String sql = "DELETE FROM customer WHERE ID = ?";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting customer: " + e.getMessage());
            return false;
        }
    }

    public static Customer getCustomerByID(int id) {
        String sql = "SELECT Name, PhoneNumber FROM customer WHERE ID = ?";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("Name");
                String phone = rs.getString("PhoneNumber");
                return new Customer(id, name, phone);
            } else {
                return null; // Customer not found
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving customer: " + e.getMessage());
            return null;
        }
    }


    public static List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customer";
        try (Connection conn = DBCon.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                customers.add(new Customer(
                        rs.getInt("ID"),
                        rs.getString("Name"),
                        rs.getString("PhoneNumber")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching customers: " + e.getMessage());
        }
        return customers;
    }


    // Customers who placed delivery orders
    public static List<Customer> getCustomersWithDeliveryOrders() {
        List<Customer> customers = new ArrayList<>();

        String sql = """
        SELECT DISTINCT c.ID, c.Name, c.PhoneNumber
        FROM Customer  c
        JOIN Orders    o  ON c.ID = o.customerID
        JOIN OrderType ot ON o.orderTypeID = ot.ID     
        WHERE ot.Name = ?                              
    """;

        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "Delivery");                 // the value in your OrderType table
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(new Customer(
                            rs.getInt("ID"),
                            rs.getString("Name"),
                            rs.getString("PhoneNumber")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving customers with delivery orders: " + e.getMessage());
        }
        return customers;
    }

    public static int getLastInsertedCustomerId() {
        String sql = "SELECT MAX(ID) AS last_id FROM Customer";
        try (Connection conn = DBCon.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("last_id");
        } catch (SQLException e) {
            System.out.println("Error retrieving last customer ID: " + e.getMessage());
        }
        return -1;
    }





}
