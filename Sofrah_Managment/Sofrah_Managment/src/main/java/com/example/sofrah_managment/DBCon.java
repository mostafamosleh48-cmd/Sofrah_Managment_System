package Sofrah_Managment.src.main.java.com.example.sofrah_managment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBCon {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/sofrah";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "123456mostafa";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }



}
