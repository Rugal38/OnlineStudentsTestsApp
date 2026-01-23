package config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DB {

//    private static final String URL = "jdbc:mysql://localhost:3306/gestion_tests";
//    private static final String USER = "root";
//    private static final String PASSWORD = "";

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/gestion_tests", "root", "");
    }
}
