package org.example;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseConnector {
    //private static Dotenv dotenv = Dotenv.load();

    private static final String URL = "jdbc:mysql://localhost:3306/order_system";
    private static final String USER = "root";
    private static final String PASSWORD = ("DB_PASSWORD");

    public DatabaseConnector() {
    }

    public static Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/order_system", "root", "charles21");
        } catch (SQLException | ClassNotFoundException e) {
            ((Exception)e).printStackTrace();
            return null;
        }
    }
}
