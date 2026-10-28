package Lab2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/igirepay";
        String username = "postgres";
        String password = "Gicanda123@";

        return DriverManager.getConnection(url, username, password);
    }
}
