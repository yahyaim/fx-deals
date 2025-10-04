package fx.wh.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

import org.jspecify.annotations.NonNull;

public class Deal {
    
    @NonNull
    private final String dealId;

    // @Pattern(regexp = "^[A-Z]{3}$") 
    private final  String fromCurrency;

    // @Pattern(regexp = "^[A-Z]{3}$") 
    private final String toCurrency;

    // @StringFormat("yyyy-MM-dd'T'HH:mm:ssXXX") 
    private final Instant timestamp;
  
    // @DecimalMin("0.0") 
    private final BigDecimal amount;

    public Deal(String dealId, String fromCurrency, String toCurrency, Instant timestamp, BigDecimal amount) {
        this.dealId = dealId;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.timestamp = timestamp;
        this.amount = amount;
    }

    public String getDealId() {return dealId;}
    public String getFromCurrency() {return fromCurrency;}
    public String getToCurrency() {return toCurrency;}
    public Instant getTimestamp() {return timestamp;}
    public BigDecimal getAmount() {return amount;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Deal deal = (Deal) o;

        if (!dealId.equals(deal.dealId)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dealId);  
    }
}
