package com.example.sofrah_managment;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ShiftManagement {

    public static List<Shift> getAllShifts() {
        List<Shift> shifts = new ArrayList<>();
        String sql = "SELECT * FROM shifts";

        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                shifts.add(buildShift(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return shifts;
    }

    public static List<Shift> getUpcomingShiftsInNextThreeDays() {
        List<Shift> shifts = new ArrayList<>();
        String sql = "SELECT * FROM shifts WHERE expectedCheckIn BETWEEN ? AND ?";

        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime threeDaysLater = now.plusDays(3);
            stmt.setTimestamp(1, Timestamp.valueOf(now));
            stmt.setTimestamp(2, Timestamp.valueOf(threeDaysLater));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    shifts.add(buildShift(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return shifts;
    }

    public static boolean addShift(Shift shift) {
        String sql = "INSERT INTO Shifts (EmployeeID, expectedCheckIn, expectedCheckOut, CheckIn, CheckOut, hasWorked) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, shift.getEmployeeId());
            stmt.setTimestamp(2, Timestamp.valueOf(shift.getExpectedCheckIn()));
            stmt.setTimestamp(3, Timestamp.valueOf(shift.getExpectedCheckOut()));
            stmt.setTimestamp(4, shift.getCheckIn() != null ? Timestamp.valueOf(shift.getCheckIn()) : null);
            stmt.setTimestamp(5, shift.getCheckOut() != null ? Timestamp.valueOf(shift.getCheckOut()) : null);
            stmt.setBoolean(6, shift.isHasWorked());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error adding shift: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateShift(Shift shift) {
        String sql = "UPDATE shifts SET CheckIn = ?, CheckOut = ?, HasWorked = ? WHERE ID = ?";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, shift.getCheckIn() != null ? Timestamp.valueOf(shift.getCheckIn()) : null);
            stmt.setTimestamp(2, shift.getCheckOut() != null ? Timestamp.valueOf(shift.getCheckOut()) : null);
            stmt.setBoolean(3, shift.isHasWorked());
            stmt.setInt(4, shift.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating shift: " + e.getMessage());
            return false;
        }
    }


    public static boolean deleteShift(int shiftId) {
        String sql = "DELETE FROM shifts WHERE id=?";

        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, shiftId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static Shift buildShift(ResultSet rs) throws SQLException {
        return new Shift(
                rs.getInt("id"),
                rs.getInt("employeeId"),
                rs.getTimestamp("checkIn") != null ? rs.getTimestamp("checkIn").toLocalDateTime() : null,
                rs.getTimestamp("checkOut") != null ? rs.getTimestamp("checkOut").toLocalDateTime() : null,
                rs.getTimestamp("expectedCheckIn").toLocalDateTime(),
                rs.getTimestamp("expectedCheckOut").toLocalDateTime(),
                rs.getBoolean("hasWorked")
        );
    }





}
