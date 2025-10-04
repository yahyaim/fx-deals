package fx.wh;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import fx.wh.model.Deal;
import fx.wh.utils.Validator;

public class ValidatorTest {
    @Test
    void validDealIsAccepted() {
        Deal d = new Deal("D-1","USD","EUR", Instant.now(), new BigDecimal("100.0"));
        Validator v = new Validator();
        var res = v.validate(d);
        assertTrue(res.valid);
    }
}
