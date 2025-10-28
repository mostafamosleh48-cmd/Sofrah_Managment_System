package com.example.sofrah_managment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderTypeManagement {

    public static List<OrderType> getAllOrderTypes() {
        List<OrderType> list = new ArrayList<>();
        String sql = "SELECT ID, Name FROM OrderType";
        try (Connection conn = DBCon.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new OrderType(rs.getInt("ID"), rs.getString("Name")));
            }
        } catch (SQLException e) {
            System.out.println("Error loading order types: " + e.getMessage());
        }
        return list;
    }


    public static String getNameById(int id) {
        String sql = "SELECT Name FROM ordertype WHERE ID = ?";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Name");
            }
        } catch (SQLException e) {
            System.out.println("Error getting order type name: " + e.getMessage());
        }
        return null;
    }
}