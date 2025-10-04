package fx.wh.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Deal {

    private static final Logger logger = LoggerFactory.getLogger(Deal.class);

    @NonNull
    private final String dealId;

    private final String fromCurrency;
    private final String toCurrency;
    private final Instant timestamp;
    private final BigDecimal amount;

    public Deal(@NonNull String dealId, String fromCurrency, String toCurrency, Instant timestamp, BigDecimal amount) {
        // Validate dealId
        if (dealId == null || dealId.isBlank()) {
            logger.error("Attempted to create Deal with null or empty dealId");
            throw new IllegalArgumentException("dealId must not be null or empty");
        }

        // Validate currency codes (basic 3-letter check)
        if (fromCurrency == null || !fromCurrency.matches("^[A-Z]{3}$")) {
            logger.warn("Invalid fromCurrency '{}', expected 3 uppercase letters", fromCurrency);
            throw new IllegalArgumentException("fromCurrency must be 3 uppercase letters");
        }
        if (toCurrency == null || !toCurrency.matches("^[A-Z]{3}$")) {
            logger.warn("Invalid toCurrency '{}', expected 3 uppercase letters", toCurrency);
            throw new IllegalArgumentException("toCurrency must be 3 uppercase letters");
        }

        // Validate timestamp
        if (timestamp == null) {
            logger.warn("Timestamp is null, setting to Instant.now()");
            timestamp = Instant.now();
        }

        // Validate amount
        if (amount == null || amount.signum() < 0) {
            logger.error("Invalid amount '{}', must be non-negative", amount);
            throw new IllegalArgumentException("amount must be non-negative");
        }

        this.dealId = dealId;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.timestamp = timestamp;
        this.amount = amount;

        logger.info("Deal created: {}", this);
    }

    public String getDealId() { return dealId; }
    public String getFromCurrency() { return fromCurrency; }
    public String getToCurrency() { return toCurrency; }
    public Instant getTimestamp() { return timestamp; }
    public BigDecimal getAmount() { return amount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Deal)) return false;
        Deal deal = (Deal) o;
        return dealId.equals(deal.dealId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dealId);
    }

    @Override
    public String toString() {
        return "Deal{" +
                "dealId='" + dealId + '\'' +
                ", fromCurrency='" + fromCurrency + '\'' +
                ", toCurrency='" + toCurrency + '\'' +
                ", timestamp=" + timestamp +
                ", amount=" + amount +
                '}';
    }
}
