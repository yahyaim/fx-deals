package fx.wh.repository;

import javax.sql.DataSource;
import java.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fx.wh.model.Deal;

public class DealRepository {

    private static final Logger logger = LoggerFactory.getLogger(DealRepository.class);
    private final DataSource datasource;

    public DealRepository(DataSource datasource) {
        if (datasource == null) {
            logger.error("Attempted to create DealRepository with null DataSource");
            throw new IllegalArgumentException("DataSource must not be null");
        }
        this.datasource = datasource;
    }

    public boolean insertDeal(Deal deal) {
        if (deal == null) {
            logger.warn("Attempted to insert null deal");
            throw new IllegalArgumentException("deal must not be null");
        }

        String sql = "INSERT INTO deals (deal_id, from_currency, to_currency, deal_timestamp, amount) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = datasource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, deal.getDealId());
            ps.setString(2, deal.getFromCurrency());
            ps.setString(3, deal.getToCurrency());
            ps.setTimestamp(4, Timestamp.from(deal.getTimestamp()));
            ps.setBigDecimal(5, deal.getAmount());

            int rowsAffected = ps.executeUpdate();
            logger.info("Inserted deal {}: {} row(s) affected", deal.getDealId(), rowsAffected);
            return rowsAffected == 1;

        } catch (SQLException e) {
            // Handle Postgres unique violation (duplicate)
            if ("23505".equals(e.getSQLState())) {
                logger.info("Deal already exists (duplicate): {}", deal.getDealId());
                return false;
            } else {
                logger.error("Database error inserting deal {}: {}", deal.getDealId(), e.getMessage(), e);
                // Optionally rethrow as unchecked exception
                // throw new RuntimeException("Failed to insert deal: " + deal.getDealId(), e);
                return false;
            }
        }
    }
}
