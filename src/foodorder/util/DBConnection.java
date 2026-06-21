package foodorder.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * Centralized JDBC connection helper.
 * Default settings match a standard XAMPP installation (root user, no password).
 * Update DB_URL / DB_USER / DB_PASS below if your MySQL setup is different.
 * Requires mysql-connector-j-8.4.0.jar to be added to the project's Libraries
 * (already referenced from the lib/ folder in this NetBeans project).
 */
public class DBConnection {

    private static final String DB_URL  = "jdbc:mysql://localhost:3306/food_ordering_db?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = ""; // XAMPP default: blank. Change if you set a MySQL password.

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                    "MySQL JDBC Driver not found.\nAdd mysql-connector-j-8.4.0.jar to the project Libraries.",
                    "Driver Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
}
