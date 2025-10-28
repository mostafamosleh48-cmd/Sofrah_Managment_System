package com.example.sofrah_managment;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderManagement {

    // Add a new order
    public static boolean addOrder(Order order) {
        String sql = "INSERT INTO orders (customerID, EmployeeID, orderStatusID, orderTypeID, Date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, order.getCustomerId());
            stmt.setInt(2, order.getEmployeeId());
            stmt.setInt(3, order.getOrderStatusId());
            stmt.setInt(4, order.getOrderTypeId());
            stmt.setTimestamp(5, Timestamp.valueOf(order.getDate()));

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error adding order: " + e.getMessage());
            return false;
        }
    }

    // Update an existing order
    public static boolean updateOrder(Order order) {
        String sql = "UPDATE orders SET customerID = ?, EmployeeID = ?, orderStatusID = ?, orderTypeID = ?, Date = ? WHERE ID = ?";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, order.getCustomerId());
            stmt.setInt(2, order.getEmployeeId());
            stmt.setInt(3, order.getOrderStatusId());
            stmt.setInt(4, order.getOrderTypeId());
            stmt.setTimestamp(5, Timestamp.valueOf(order.getDate()));
            stmt.setInt(6, order.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating order: " + e.getMessage());
            return false;
        }
    }
    public static boolean updateOrderItem(OrderItem item) {
        String sql = "UPDATE OrderMenuItem SET menuItemID = ?, quantity = ? WHERE orderID = ?";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, item.getMenuItemId());
            stmt.setInt(2, item.getQuantity());
            stmt.setInt(3, item.getOrderId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating order item: " + e.getMessage());
            return false;
        }
    }


    // Delete an order by ID
    public static boolean deleteOrder(int id) {
        String sql = "DELETE FROM orders WHERE ID = ?";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting order: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteOrderItem(int orderId, int menuItemId) {
        String sql = "DELETE FROM OrderMenuItem WHERE orderID = ? AND menuItemID = ?";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            stmt.setInt(2, menuItemId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting order item: " + e.getMessage());
            return false;
        }
    }


    // Retrieve a single order by ID
    public static Order getOrderById(int id) {
        String sql = "SELECT * FROM orders WHERE ID = ?";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Order(
                        rs.getInt("ID"),
                        rs.getInt("customerID"),
                        rs.getInt("EmployeeID"),
                        rs.getInt("orderStatusID"),
                        rs.getInt("orderTypeID"),
                        rs.getTimestamp("Date").toLocalDateTime()
                );
            }
        } catch (SQLException e) {
            System.out.println("Error fetching order: " + e.getMessage());
        }
        return null;
    }

    public static String getLocationForCustomer(int customerId) {
        String sql = """
        SELECT l.City, l.Street
        FROM CustomerLocation cl
        JOIN Location l ON cl.LocationID = l.ID
        WHERE cl.CustomerID = ?
        LIMIT 1
    """;
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("City") + ", " + rs.getString("Street");
            }
        } catch (SQLException e) {
            System.out.println("Error getting location: " + e.getMessage());
        }
        return "Unknown";
    }


    // Retrieve all orders
    public static List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";

        try (Connection conn = DBCon.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                orders.add(new Order(
                        rs.getInt("ID"),
                        rs.getInt("customerID"),
                        rs.getInt("EmployeeID"),
                        rs.getInt("orderStatusID"),
                        rs.getInt("orderTypeID"),
                        rs.getTimestamp("Date").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving orders: " + e.getMessage());
        }

        return orders;
    }

    // Retrieve orders by specific date
    public static List<Order> getOrdersByDate(LocalDate date) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE DATE(Date) = ?";

        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                orders.add(new Order(
                        rs.getInt("ID"),
                        rs.getInt("customerID"),
                        rs.getInt("EmployeeID"),
                        rs.getInt("orderStatusID"),
                        rs.getInt("orderTypeID"),
                        rs.getTimestamp("Date").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving orders by date: " + e.getMessage());
        }

        return orders;
    }

    // Retrieve orders by customer ID
    public static List<Order> getOrdersByCustomer(int customerId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE customerID = ?";

        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                orders.add(new Order(
                        rs.getInt("ID"),
                        rs.getInt("customerID"),
                        rs.getInt("EmployeeID"),
                        rs.getInt("orderStatusID"),
                        rs.getInt("orderTypeID"),
                        rs.getTimestamp("Date").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving orders by customer: " + e.getMessage());
        }

        return orders;
    }

    // Retrieve orders by order type (like Dine-in, Delivery, etc.)
    public static List<Order> getOrdersByType(String typeName) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.* FROM orders o JOIN ordertype t ON o.orderTypeID = t.ID WHERE t.Name = ?";

        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, typeName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                orders.add(new Order(
                        rs.getInt("ID"),
                        rs.getInt("customerID"),
                        rs.getInt("EmployeeID"),
                        rs.getInt("orderStatusID"),
                        rs.getInt("orderTypeID"),
                        rs.getTimestamp("Date").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving orders by type: " + e.getMessage());
        }

        return orders;
    }



    public static List<OrderItem> getOrderItemsByOrderId(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT menuItemID, orderID, quantity FROM OrderMenuItem WHERE orderID = ?";

        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                items.add(new OrderItem(
                        0, // or remove this if your OrderItem class does not require it
                        rs.getInt("menuItemID"),
                        rs.getInt("orderID"),
                        rs.getInt("quantity")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving order items: " + e.getMessage());
        }

        return items;
    }

    public static boolean addOrderItem(OrderItem item) {
        String sql = "INSERT INTO OrderMenuItem (orderID, menuItemID, quantity) VALUES (?, ?, ?)";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, item.getOrderId());
            stmt.setInt(2, item.getMenuItemId());
            stmt.setInt(3, item.getQuantity());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error adding order item: " + e.getMessage());
            return false;
        }
    }


    public static List<MenuItem> getAvailableMenuItems() {
        List<MenuItem> list = new ArrayList<>();
        String sql = "SELECT * FROM MenuItem WHERE isAvailable = TRUE";

        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                MenuItem item = new MenuItem(
                        rs.getInt("ID"),
                        rs.getString("ItemName"),
                        rs.getString("Description"),
                        rs.getBoolean("isAvailable"),
                        rs.getDouble("Price")
                );
                list.add(item);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching available menu items: " + e.getMessage());
        }

        return list;
    }

    public static int getLastInsertedOrderId() {
        String sql = "SELECT MAX(ID) AS last_id FROM orders";
        try (Connection conn = DBCon.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("last_id");
            }
        } catch (SQLException e) {
            System.out.println("Error getting last order ID: " + e.getMessage());
        }
        return -1;
    }


    // OrderManagement.java

    public static String getStatusNameById(int id) {
        String sql = "SELECT Name FROM OrderStatus WHERE ID = ?";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("Name");
        } catch (SQLException e) {
            System.out.println("Error fetching order status name: " + e.getMessage());
        }
        return "Unknown";
    }

    public static String getTypeNameById(int id) {
        String sql = "SELECT Name FROM OrderType WHERE ID = ?";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("Name");
        } catch (SQLException e) {
            System.out.println("Error fetching order type name: " + e.getMessage());
        }
        return "Unknown";
    }


    public static List<String> getProfitByOrderType() {
        List<String> result = new ArrayList<>();
        String sql = """
        SELECT t.Name AS orderTypeName, SUM(mi.Price * omi.quantity) AS totalProfit
        FROM Orders o
        JOIN OrderType t ON o.orderTypeID = t.ID
        JOIN OrderMenuItem omi ON o.ID = omi.orderID
        JOIN MenuItem mi ON omi.menuItemID = mi.ID
        GROUP BY t.Name
    """;

        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String type = rs.getString("orderTypeName");
                double profit = rs.getDouble("totalProfit");
                result.add(type + ": " + profit);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving profit by order type: " + e.getMessage());
        }

        return result;
    }
    public static List<String> getTotalSalesByMonth(String monthStr) { // input format is: 2025-06
        List<String> result = new ArrayList<>();
        String sql = "SELECT SUM(mi.price * omi.quantity) AS total_sales " +
                "FROM Orders o " +
                "JOIN OrderMenuItem omi ON o.ID = omi.orderID " +
                "JOIN MenuItem mi ON omi.menuItemID = mi.ID " +
                "WHERE DATE_FORMAT(o.Date, '%Y-%m') = ?";   // convert sql date to the same format


        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, monthStr); // Format: "2025-06"
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double total = rs.getDouble("total_sales");
                result.add("Total Sales in " + monthStr + ": " + total + " NIS");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching monthly sales: " + e.getMessage());
        }
        return result;
    }













}
