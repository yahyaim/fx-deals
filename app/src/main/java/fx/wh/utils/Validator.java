package fx.wh.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fx.wh.model.Deal;

public class Validator {

    private static final Logger logger = LoggerFactory.getLogger(Validator.class);

    public static class Result {
        public final boolean valid;
        public final List<String> errors;

        public Result(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }
    }

    /**
     * Validates a Deal object and returns validation result.
     */
    public Result validate(Deal d) {
        List<String> errors = new ArrayList<>();

        if (d == null) {
            String msg = "Deal is null";
            logger.warn(msg);
            errors.add(msg);
            return new Result(false, errors);
        }

        if (isBlank(d.getDealId())) {
            String msg = "deal_uid is missing";
            logger.warn(msg);
            errors.add(msg);
        }

        if (isBlank(d.getFromCurrency()) || d.getFromCurrency().length() != 3) {
            String msg = "from_currency invalid: " + d.getFromCurrency();
            logger.warn(msg);
            errors.add(msg);
        }

        if (isBlank(d.getToCurrency()) || d.getToCurrency().length() != 3) {
            String msg = "to_currency invalid: " + d.getToCurrency();
            logger.warn(msg);
            errors.add(msg);
        }

        if (d.getTimestamp() == null) {
            String msg = "deal_timestamp missing";
            logger.warn(msg);
            errors.add(msg);
        }

        if (d.getAmount() == null || d.getAmount().signum() <= 0) {
            String msg = "amount must be > 0: " + d.getAmount();
            logger.warn(msg);
            errors.add(msg);
        }

        boolean isValid = errors.isEmpty();
        if (isValid) {
            logger.debug("Deal {} passed validation", d.getDealId());
        } else {
            logger.debug("Deal {} failed validation with errors: {}", 
                         d.getDealId() != null ? d.getDealId() : "null", errors);
        }

        return new Result(isValid, errors);
    }

    /**
     * Convenience method to quickly check if a deal is valid.
     */
    public boolean isValid(Deal d) {
        return validate(d).valid;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
