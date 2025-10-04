package fx.wh;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fx.wh.model.Deal;
import fx.wh.repository.DealRepository;
import java.sql.Connection;

public class DealRepositoryTest {
    private DataSource dataSource;
    private Connection connection;
    private PreparedStatement statement;
    private DealRepository repository;

    @BeforeEach
    void setup() throws SQLException {
        dataSource = mock(DataSource.class);
        connection = mock(Connection.class);
        statement = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        repository = new DealRepository(dataSource);
    }

    @Test
    void insertDealSuccess() throws SQLException {
        Deal deal = new Deal("D1", "USD", "EUR", Instant.now(), BigDecimal.TEN);
        when(statement.executeUpdate()).thenReturn(1);
        boolean inserted = repository.insertDeal(deal);
        assertTrue(inserted);
        verify(statement).setString(1, "D1");
    }

    @Test
    void insertDealDuplicate() throws SQLException {
        Deal deal = new Deal("D1", "USD", "EUR", Instant.now(), BigDecimal.TEN);
        SQLException sqlEx = new SQLException("duplicate key", "23505");
        when(statement.executeUpdate()).thenThrow(sqlEx);
        boolean inserted = repository.insertDeal(deal);
        assertFalse(inserted);
    }

    @Test
    void insertDealOtherSQLException() throws SQLException {
        Deal deal = new Deal("D1", "USD", "EUR", Instant.now(), BigDecimal.TEN);
        SQLException sqlEx = new SQLException("connection error", "08001");
        when(statement.executeUpdate()).thenThrow(sqlEx);
        boolean inserted = repository.insertDeal(deal);
        assertFalse(inserted);
    }
}
