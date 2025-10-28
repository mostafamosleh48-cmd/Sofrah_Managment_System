package com.example.sofrah_managment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuOrderManagement {

    public static List<Order> getAllMenuOrders() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM Orders";

        try (Connection conn = DBCon.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Order o = new Order(
                        rs.getInt("ID"),
                        rs.getInt("customerID"),
                        rs.getInt("EmployeeID"),
                        rs.getInt("orderStatusID"),
                        rs.getInt("orderTypeID"),
                        rs.getTimestamp("Date").toLocalDateTime()
                );
                list.add(o);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching menu orders: " + e.getMessage());
        }

        return list;
    }
}
