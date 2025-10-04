package fx.wh;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import fx.wh.model.Deal;
import fx.wh.utils.Parser;

public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseValidLine() {
        String line = "D-1,USD,EUR,2025-10-04T12:00:00Z,100.5";
        Deal deal = parser.parseLine(line);
        assertEquals("D-1", deal.getDealId());
        assertEquals("USD", deal.getFromCurrency());
        assertEquals("EUR", deal.getToCurrency());
        assertEquals(Instant.parse("2025-10-04T12:00:00Z"), deal.getTimestamp());
        assertEquals(BigDecimal.valueOf(100.5), deal.getAmount());
    }

    @Test
    void parseLineInvalidFormat() {
        String line = "D-1,USD,EUR,2025-10-04T12:00:00Z"; // missing amount
        Exception ex = assertThrows(IllegalArgumentException.class, () -> parser.parseLine(line));
        assertTrue(ex.getMessage().contains("Expected 5 fields"));
    }

    @Test
    void parseLineInvalidAmount() {
        String line = "D-1,USD,EUR,2025-10-04T12:00:00Z,abc";
        Exception ex = assertThrows(IllegalArgumentException.class, () -> parser.parseLine(line));
        assertTrue(ex.getMessage().contains("Parse error"));
    }
}
