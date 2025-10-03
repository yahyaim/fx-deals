package fx.wh.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

public class DatabaseConfig {
    private static HikariDataSource source;

    public static DataSource getDataSource() {
        if (source == null) {
            HikariConfig config = new HikariConfig();

            String host = System.getenv().getOrDefault("DB_HOST", "localhost");
            String port = System.getenv().getOrDefault("DB_PORT", "5432");
            String db   = System.getenv().getOrDefault("DB_NAME", "fxdb");
            String user = System.getenv().getOrDefault("DB_USER", "fxuser");
            String pass = System.getenv().getOrDefault("DB_PASSWORD", "fxpass");

            String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, db);
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(user);
            config.setPassword(pass);

            // optional tuning
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(30000);

            source = new HikariDataSource(config);
        }
        return source;
    }

}
