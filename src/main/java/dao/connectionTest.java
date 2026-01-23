package dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class connectionTest {
    public static void main(String[] args) {
        try {
            // Test with NO database first
            Connection conn1 = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/", 
                "root", 
                ""
            );
            System.out.println("✓ Connected to MySQL server");
            conn1.close();
            
            // Test with your database
            Connection conn2 = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/gestion_tests", 
                "root", 
                ""
            );
            System.out.println("✓ Connected to 'gestion_tests' database");
            conn2.close();
            
        } catch (Exception e) {
            System.out.println("✗ Connection failed!");
            e.printStackTrace();
        }
    }
}