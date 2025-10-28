package com.example.sofrah_managment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeManagement {

    public static boolean addEmployee(Employee employee) {
        String sql = "INSERT INTO employee (jobTitleID, DateOfBirth, Name) VALUES (?, ?, ?)";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employee.getJobTitleId());
            stmt.setDate(2, Date.valueOf(employee.getDateOfBirth()));
            stmt.setString(3, employee.getName());

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding employee: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateEmployee(Employee employee) {
        String query = "UPDATE Employee SET jobTitleID = ?, DateOfBirth = ?, Name = ? WHERE ID = ?";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, employee.getJobTitleId());
            stmt.setDate(2, Date.valueOf(employee.getDateOfBirth()));
            stmt.setString(3, employee.getName());
            stmt.setInt(4, employee.getId());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating employee: " + e.getMessage());
            return false;
        }
    }

        public static boolean deleteEmployee(int id) {
            if (id <= 0) {
                System.out.println("Invalid employee ID.");
                return false;
            }

            String sql = "DELETE FROM employee WHERE ID = ?";

            try (Connection conn = DBCon.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, id);
                int affected = stmt.executeUpdate();

                if (affected > 0) {
                    return true;
                } else {
                    System.out.println("Employee not found.");
                    return false;
                }

            } catch (SQLException e) {
                if (e.getMessage().contains("a foreign key constraint fails")) {
                    System.out.println("Cannot delete: Employee is assigned to existing orders.");
                } else {
                    System.out.println("Database error: " + e.getMessage());
                }
                return false;
            }
        }

        public static Employee getEmployee(int id) {
        String sql = "SELECT * FROM employee WHERE ID = ?";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Employee(
                        rs.getInt("ID"),
                        rs.getInt("jobTitleID"),
                        rs.getDate("DateOfBirth").toLocalDate(),
                        rs.getString("Name")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving employee: " + e.getMessage());
        }
        return null;
    }


    public static List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employee";
        try (Connection conn = DBCon.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                employees.add(new Employee(
                        rs.getInt("ID"),
                        rs.getInt("jobTitleID"),
                        rs.getDate("DateOfBirth").toLocalDate(),
                        rs.getString("Name")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving employees: " + e.getMessage());
        }
        return employees;
    }

    // 4) Employees with their working hours and total salaries
    public static List<String> getEmployeesWorkHoursAndSalaries() {
        List<String> results = new ArrayList<>();
        String sql = """
      
              SELECT\s
                                            e.Name AS employeeName,
                                            jt.PayRate,
                                            ROUND(SUM(TIMESTAMPDIFF(MINUTE, s.CheckIn, s.CheckOut)) / 60, 2) AS totalHours,
                                            ROUND(SUM(TIMESTAMPDIFF(MINUTE, s.CheckIn, s.CheckOut)) / 60 * jt.PayRate, 2) AS totalSalary
                                        FROM Employee e
                                        JOIN JobTitle jt ON e.jobTitleID = jt.ID
                                        JOIN Shifts s ON e.ID = s.EmployeeID
                                        WHERE s.hasWorked = TRUE\s
                                          AND s.CheckIn IS NOT NULL\s
                                          AND s.CheckOut IS NOT NULL
                                          AND s.CheckOut > s.CheckIn
                                        GROUP BY e.ID, e.Name, jt.PayRate
                                        ORDER BY e.Name;
                        
        """;

        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("employeeName");
                double rate = rs.getDouble("PayRate");
                int hours = rs.getInt("totalHours");
                double salary = rs.getDouble("totalSalary");
                results.add(String.format("%s | Hours: %d | Rate: %.2f | Salary: %.2f", name, hours, rate, salary));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching work hours and salaries: " + e.getMessage());
        }

        return results;
    }



    // 5) Employees by job title
    public static List<Employee> getEmployeesByJobTitle(String jobTitleName) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.* FROM Employee e " +
                "JOIN JobTitle jt ON e.jobTitleID = jt.ID " +
                "WHERE jt.Name = ?";

        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, jobTitleName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                employees.add(new Employee(
                        rs.getInt("ID"),
                        rs.getInt("jobTitleID"),
                        rs.getDate("DateOfBirth").toLocalDate(),
                        rs.getString("Name")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving employees by job title: " + e.getMessage());
        }
        return employees;
    }

    // 6) Employees with upcoming shifts in next 3 days
    public static List<Employee> getEmployeesWithUpcomingShifts() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT DISTINCT e.* FROM Employee e " +
                "JOIN Shifts s ON e.ID = s.EmployeeID " +
                "WHERE s.expectedCheckIn BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL 3 DAY)";

        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                employees.add(new Employee(
                        rs.getInt("ID"),
                        rs.getInt("jobTitleID"),
                        rs.getDate("DateOfBirth").toLocalDate(),
                        rs.getString("Name")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving employees with upcoming shifts: " + e.getMessage());
        }
        return employees;
    }

    // 7) How many days off each employee has taken
    public static List<String> getEmployeeDaysOff() {
        List<String> results = new ArrayList<>();
        String sql = "SELECT e.Name, COUNT(*) AS DaysOff FROM Employee e " +
                "JOIN Shifts s ON e.ID = s.EmployeeID " +
                "WHERE s.hasWorked = FALSE " +
                "GROUP BY e.ID, e.Name";

        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String entry = rs.getString("Name") + " - Days Off: " + rs.getInt("DaysOff");
                results.add(entry);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving employee days off: " + e.getMessage());
        }
        return results;
    }






}




