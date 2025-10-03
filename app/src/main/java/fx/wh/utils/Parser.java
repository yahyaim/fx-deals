package fx.wh.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.*;

import fx.wh.model.Deal;

public class Parser {
     //inner class to hold parsing result
    public static class Result {
        public final int rowNumber;
        public final Deal deal;
        public final List<String> errors;
        public Result(int rowNumber, Deal deal, List<String> errors) {
            this.rowNumber = rowNumber; this.deal = deal; this.errors = errors;
        }
    }

    public List<Result> parse(File csvFile) throws IOException{
        List<Result> results = new ArrayList<>();
        try (Reader in = new FileReader(csvFile)) {
            CSVFormat format = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withTrim();
            Iterable<CSVRecord> records = format.parse(in);
            int row = 1;
            for (CSVRecord r : records) {
                List<String> errors = new ArrayList<>();
                String uid = r.get("deal_uid");
                String from = r.get("from_currency");
                String to = r.get("to_currency");
                String ts = r.get("deal_timestamp");
                String amt = r.get("amount");

                Deal deal = null;
                try {
                    Instant instant = Instant.parse(ts);
                    BigDecimal amount = new BigDecimal(amt);
                    deal = new Deal(uid, from, to, instant, amount);
                } catch (Exception e) {
                    errors.add("Parse error: " + e.getMessage());
                }
                results.add(new Result(row++, deal, errors));
            }
        }
        return results;
    
    }
}
