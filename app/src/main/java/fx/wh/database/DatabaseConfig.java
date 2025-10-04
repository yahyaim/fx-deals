package fx.wh.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConfig {
    private static HikariDataSource ds;
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    public static DataSource getDataSource() {
        if (ds == null) {
            try {
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
                logger.info("HikariCP datasource initialized successfully.");
            } catch (Exception e) {
                logger.error("Failed to initialize HikariCP datasource", e);
                throw new RuntimeException("Database initialization failed", e);
            }
        }
        return ds;
    }

    public static void close() {
        if (ds != null) {
            try {
                ds.close();
                logger.info("HikariCP datasource closed.");
            } catch (Exception e) {
                logger.warn("Failed to close HikariCP datasource", e);
            }
        }
    }
}
