package fx.wh.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PostgresClient {
    public void testConnection() {
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT 1");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                System.out.println("Connected to Postgres! Result: " + rs.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to connect to Postgres.");
        }
    }
    
}
