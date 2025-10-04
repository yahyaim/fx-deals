package fx.wh.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fx.wh.utils.Parser;
import fx.wh.utils.Validator;
import fx.wh.repository.DealRepository;
import java.io.File;
import java.util.List;

public class DealService {
    private static final Logger logger = LoggerFactory.getLogger(DealService.class);
    private final Parser parser;
    private final Validator validator;
    private final DealRepository repository;

    public DealService(Parser parser, Validator validator, DealRepository repository) {
        this.parser = parser;
        this.validator = validator;
        this.repository = repository;
    }

    public void processFile(File csvFile) {
        try {
            List<Parser.Result> results = parser.parse(csvFile);
            int total = results.size();
            int success = 0;
            int failed = 0;
            int duplicates = 0;

            for (Parser.Result r : results) {
                if (!r.errors.isEmpty()) {
                    logger.warn("Row {}: Parse errors: {}", r.rowNumber, r.errors);
                    failed++;
                    continue;
                }
                Validator.Result vres = validator.validate(r.deal);
                if (!vres.valid) {
                    logger.warn("Row {}: Validation errors: {}", r.rowNumber, vres.errors);
                    failed++;
                    continue;
                }
                boolean inserted = repository.insertDeal(r.deal);
                if (inserted) {
                    success++;
                } else {
                    // insertDeal logs duplicates and DB errors
                    duplicates++;
                }
            }
            logger.info("Processing complete. Total: {}, Successful: {}, Failed: {}, Duplicates: {}",
                        total, success, failed, duplicates);
        } catch (Exception e) {
            logger.error("Error processing file {}: {}", csvFile.getName(), e.getMessage(), e);
        }
    }

    public void importFile(File csvFile) {
        try {
            List<Parser.Result> rows = parser.parse(csvFile);
            int rowNum = 1;
            for (Parser.Result r : rows) {
                if (!r.errors.isEmpty()) {
                    logger.warn("Row {} parse errors: {}", r.rowNumber, r.errors);
                    continue;
                }
                var v = validator.validate(r.deal);
                if (!v.valid) {
                    logger.warn("Row {} validation failed: {}", r.rowNumber, v.errors);
                    continue;
                }
                boolean inserted = repository.insertDeal(r.deal);
                if (inserted) {
                    logger.info("Row {} inserted deal {}", r.rowNumber, r.deal.getDealId());
                } else {
                    logger.info("Row {} not inserted (duplicate or error) {}", r.rowNumber, r.deal.getDealId());
                }
                rowNum++;
            }
        } catch (Exception e) {
            logger.error("Failed to import file {}: {}", csvFile.getAbsolutePath(), e.getMessage(), e);
            // We do not rollback previous rows — requirement
        }
    }

    public void importLine(String line) throws Exception {
        // Example expected format:
        // D-1234,USD,EUR,2025-10-04T12:00:00Z,1000.50
        String[] parts = line.split(",");
        if (parts.length != 5) {
            throw new IllegalArgumentException("Expected 5 fields but got " + parts.length);
        }
        String uid = parts[0].trim();
        String from = parts[1].trim();
        String to = parts[2].trim();
        String ts = parts[3].trim();
        String amt = parts[4].trim();

        var deal = new fx.wh.model.Deal(uid, from, to, java.time.Instant.parse(ts), new java.math.BigDecimal(amt));
        var v = validator.validate(deal);
        if (!v.valid) {
            throw new IllegalArgumentException("Validation failed: " + v.errors);
        }
        boolean inserted = repository.insertDeal(deal);
        if (inserted) {
            logger.info("Inserted single deal {}", deal.getDealId());
        } else {
            logger.info("Single deal not inserted (duplicate or error) {}", deal.getDealId());
        }
    }

    public void importSingleLine(String line) {
        var deal = parser.parseLine(line);
        if (validator.isValid(deal)) {
            repository.insertDeal(deal);
        } else {
            logger.info("Invalid deal format:", deal);
        }
    }

}
