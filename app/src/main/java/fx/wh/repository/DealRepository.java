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
        this.datasource = datasource;
    }

    private static final String INSERT_SQL =
    "INSERT INTO deals (deal_uid, from_currency, to_currency, deal_timestamp, amount) VALUES (?, ?, ?, ?, ?)";

    public boolean insertDeal(Deal deal) {
        String sql = "INSERT INTO deals (deal_id, from_currency, to_currency, deal_timestamp, amount) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = datasource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, deal.getDealId());
            ps.setString(2, deal.getFromCurrency());
            ps.setString(3, deal.getToCurrency());
            ps.setTimestamp(4, Timestamp.from(deal.getTimestamp()));
            ps.setBigDecimal(5, deal.getAmount());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            // Unique violation (Postgres SQLState 23505)
            if ("23505".equals(e.getSQLState())) {
                logger.info("Deal already exists (duplicate): {}", deal.getDealId());
                return false;
            } else {
                logger.error("DB error inserting deal {}: {}", deal.getDealId(), e.getMessage(), e);
                // depending on policy, we may rethrow a runtime exception; for now return false
                return false;
            }
        }
    }
    
}
