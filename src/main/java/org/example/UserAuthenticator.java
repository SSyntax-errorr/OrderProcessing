package org.example;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAuthenticator {
    public UserAuthenticator() {
    }

    public static String authenticate(String username, String password) {
        String sql = "SELECT role FROM Users WHERE username=? AND password=?";

        try {
            String var6;
            try (Connection conn = DatabaseConnector.connect()) {
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, username);
                    stmt.setString(2, password);
                    ResultSet rs = stmt.executeQuery();
                    if (!rs.next()) {
                        return null;
                    }

                    var6 = rs.getString("role");
                }
            }

            return var6;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
