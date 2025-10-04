package fx.wh.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DatabaseConfig {
    private static HikariDataSource ds;

    public static DataSource getDataSource() {
        if (ds == null) {
            String host = System.getenv().getOrDefault("DB_HOST", "localhost");
            String port = System.getenv().getOrDefault("DB_PORT", "5432");
            String db = System.getenv().getOrDefault("DB_NAME", "fxdb");
            String user = System.getenv().getOrDefault("DB_USER", "fxuser");
            String pass = System.getenv().getOrDefault("DB_PASS", "fxpass");

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:postgresql://" + host + ":" + port + "/" + db);
            config.setUsername(user);
            config.setPassword(pass);

            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setAutoCommit(true);
            config.setPoolName("HikariPool-1");

            ds = new HikariDataSource(config);
        }
        return ds;
    }

    public static void close() {
        if (ds != null) {
            ds.close();
        }
    }
}
