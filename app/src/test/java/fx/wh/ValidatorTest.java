package fx.wh;

import fx.wh.model.Deal;
import fx.wh.utils.Validator;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

    private final Validator validator = new Validator();

    @Test
    void validateNullDeal() {
        Validator.Result result = validator.validate(null);
        assertFalse(result.valid);
        assertTrue(result.errors.contains("Deal is null"));
    }

    @Test
    void validateCorrectDeal() {
        Deal deal = new Deal("D1", "USD", "EUR", Instant.now(), BigDecimal.TEN);
        Validator.Result result = validator.validate(deal);
        assertTrue(result.valid);
        assertTrue(result.errors.isEmpty());
    }
}
