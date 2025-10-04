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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fx.wh.model.Deal;

public class Parser {

    private static final Logger logger = LoggerFactory.getLogger(Parser.class);

    // Inner class to hold parsing result
    public static class Result {
        public final int rowNumber;
        public final Deal deal;
        public final List<String> errors;

        public Result(int rowNumber, Deal deal, List<String> errors) {
            this.rowNumber = rowNumber;
            this.deal = deal;
            this.errors = errors;
        }
    }

    public List<Result> parse(File csvFile) {
        List<Result> results = new ArrayList<>();

        if (csvFile == null || !csvFile.exists()) {
            logger.error("CSV file is null or does not exist: {}", csvFile);
            return results; // return empty list
        }

        try (Reader in = new FileReader(csvFile)) {
            CSVFormat format = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withTrim();
            Iterable<CSVRecord> records = format.parse(in);

            int row = 1;
            for (CSVRecord r : records) {
                List<String> errors = new ArrayList<>();
                Deal deal = null;

                try {
                    String uid = r.get("deal_uid");
                    String from = r.get("from_currency");
                    String to = r.get("to_currency");
                    String ts = r.get("deal_timestamp");
                    String amt = r.get("amount");

                    Instant instant = Instant.parse(ts);
                    BigDecimal amount = new BigDecimal(amt);
                    deal = new Deal(uid, from, to, instant, amount);

                } catch (IllegalArgumentException | NullPointerException e) {
                    String msg = "Parse error at row " + row + ": " + e.getMessage();
                    logger.warn(msg, e);
                    errors.add(msg);
                } catch (Exception e) {
                    String msg = "Unexpected error at row " + row + ": " + e.getMessage();
                    logger.error(msg, e);
                    errors.add(msg);
                }

                results.add(new Result(row++, deal, errors));
            }

        } catch (IOException e) {
            logger.error("Failed to read CSV file {}: {}", csvFile.getAbsolutePath(), e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error parsing file {}: {}", csvFile.getAbsolutePath(), e.getMessage(), e);
        }

        return results;
    }

    public Deal parseLine(String line) {
        if (line == null || line.isBlank()) {
            logger.warn("Skipped empty or null line");
            return null;
        }

        String[] parts = line.split(",");
        if (parts.length != 5) {
            String msg = "Expected 5 fields but got " + parts.length + " in line: " + line;
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }

        try {
            String uid = parts[0].trim();
            String from = parts[1].trim();
            String to = parts[2].trim();
            Instant instant = Instant.parse(parts[3].trim());
            BigDecimal amount = new BigDecimal(parts[4].trim());

            return new Deal(uid, from, to, instant, amount);

        } catch (Exception e) {
            String msg = "Parse error for line: " + line + " -> " + e.getMessage();
            logger.error(msg, e);
            throw new IllegalArgumentException(msg, e);
        }
    }
}
