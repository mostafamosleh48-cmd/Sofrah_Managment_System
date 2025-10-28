package com.example.sofrah_managment;
import javafx.scene.control.Alert;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class JobTitleManagement {

    public static List<JobTitle> getAllJobTitles() {
        List<JobTitle> titles = new ArrayList<>();
        String sql = "SELECT * FROM jobtitle";
        try (Connection conn = DBCon.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                titles.add(new JobTitle(
                        rs.getInt("ID"),
                        rs.getString("Name"),
                        rs.getString("Description"),
                        rs.getDouble("PayRate")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error loading job titles: " + e.getMessage());
        }
        return titles;
    }

    public static String getNameById(int id) {
        String sql = "SELECT Name FROM jobtitle WHERE ID = ?";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Name");
            }
        } catch (SQLException e) {
            System.out.println("Error getting job title name: " + e.getMessage());
        }
        return null;
    }

    private static boolean addJobTitle(JobTitle job) {
        String sql = "INSERT INTO jobtitle (Name, Description, PayRate) VALUES (?, ?, ?)";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, job.getName());
            stmt.setString(2, job.getDescription());
            stmt.setDouble(3, job.getPayRate());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error adding job title: " + e.getMessage());
            return false;
        }
    }


}



