package Sofrah_Managment.src.main.java.com.example.sofrah_managment;

import java.sql.*;


public class SupplierDAO {


    public int addSupplier(Supplier supplier) throws SQLException {
        String sql = "INSERT INTO Supplier (Name, phoneNum) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        int supplierId = -1;

        try {
            conn = DBCon.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, supplier.getName());
            pstmt.setString(2, supplier.getPhoneNum());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    supplierId = generatedKeys.getInt(1);
                    System.out.println("Supplier added successfully with ID: " + supplierId);
                }
            }
            return supplierId;
        } finally {
            if (generatedKeys != null) generatedKeys.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    public Supplier getSupplierById(int id) throws SQLException {
        String sql = "SELECT ID, Name, Phone FROM Supplier WHERE ID = ?";
        try (Connection conn = DBCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Supplier(rs.getInt("ID"), rs.getString("Name"), rs.getString("Phone"));
                }
            }
        }
        return null;
    }

}