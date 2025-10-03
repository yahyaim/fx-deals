package fx.wh.utils;

import java.util.ArrayList;
import java.util.List;

import fx.wh.model.Deal;

public class Validator {
    public static class Result {
        public final boolean valid;
        public final List<String> errors;
        public Result(boolean valid, List<String> errors) {
            this.valid = valid; this.errors = errors;
        }
    }

    public Result validate(Deal d) {
        List<String> errors = new ArrayList<>();
        if (d == null) {
            errors.add("Deal is null");
            return new Result(false, errors);
        }
        if (isBlank(d.getDealId())) errors.add("deal_uid is missing");
        if (isBlank(d.getFromCurrency()) || d.getFromCurrency().length() != 3) errors.add("from_currency invalid");
        if (isBlank(d.getToCurrency()) || d.getToCurrency().length() != 3) errors.add("to_currency invalid");
        if (d.getTimestamp() == null) errors.add("deal_timestamp missing");
        if (d.getAmount() == null || d.getAmount().signum() <= 0) errors.add("amount must be > 0");
        return new Result(errors.isEmpty(), errors);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
