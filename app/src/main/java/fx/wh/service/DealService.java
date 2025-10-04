package fx.wh.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fx.wh.utils.Parser;
import fx.wh.utils.Validator;
import fx.wh.repository.DealRepository;
import fx.wh.model.Deal;

import java.io.File;
import java.util.List;

public class DealService {

    private static final Logger logger = LoggerFactory.getLogger(DealService.class);

    private final Parser parser;
    private final Validator validator;
    private final DealRepository repository;

    public DealService(Parser parser, Validator validator, DealRepository repository) {
        if (parser == null || validator == null || repository == null) {
            logger.error("DealService initialized with null dependency");
            throw new IllegalArgumentException("Parser, Validator, and Repository must not be null");
        }
        this.parser = parser;
        this.validator = validator;
        this.repository = repository;
    }

    public void processFile(File csvFile) {
        if (csvFile == null || !csvFile.exists()) {
            logger.error("CSV file is null or does not exist: {}", csvFile);
            return;
        }

        try {
            List<Parser.Result> results = parser.parse(csvFile);
            int total = results.size();
            int success = 0;
            int failed = 0;
            int duplicates = 0;

            for (Parser.Result r : results) {
                if (!r.errors.isEmpty()) {
                    logger.warn("Row {} parse errors: {}", r.rowNumber, r.errors);
                    failed++;
                    continue;
                }

                Validator.Result vres = validator.validate(r.deal);
                if (!vres.valid) {
                    logger.warn("Row {} validation errors: {}", r.rowNumber, vres.errors);
                    failed++;
                    continue;
                }

                boolean inserted = repository.insertDeal(r.deal);
                if (inserted) {
                    success++;
                } else {
                    duplicates++;
                }
            }

            logger.info("Processing complete for file {}. Total: {}, Successful: {}, Failed: {}, Duplicates: {}",
                        csvFile.getName(), total, success, failed, duplicates);

        } catch (Exception e) {
            logger.error("Error processing file {}: {}", csvFile.getAbsolutePath(), e.getMessage(), e);
        }
    }

    public void importFile(File csvFile) {
        if (csvFile == null || !csvFile.exists()) {
            logger.error("CSV file is null or does not exist: {}", csvFile);
            return;
        }

        try {
            List<Parser.Result> rows = parser.parse(csvFile);

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
            }

        } catch (Exception e) {
            logger.error("Failed to import file {}: {}", csvFile.getAbsolutePath(), e.getMessage(), e);
        }
    }

    public void importLine(String line) {
        if (line == null || line.isBlank()) {
            logger.warn("Skipped empty or null line");
            return;
        }

        try {
            String[] parts = line.split(",");
            if (parts.length != 5) {
                logger.error("Line has incorrect number of fields (expected 5): {}", line);
                return;
            }

            Deal deal = new Deal(
                    parts[0].trim(),
                    parts[1].trim(),
                    parts[2].trim(),
                    java.time.Instant.parse(parts[3].trim()),
                    new java.math.BigDecimal(parts[4].trim())
            );

            var v = validator.validate(deal);
            if (!v.valid) {
                logger.warn("Validation failed for line {}: {}", line, v.errors);
                return;
            }

            boolean inserted = repository.insertDeal(deal);
            if (inserted) {
                logger.info("Inserted single deal {}", deal.getDealId());
            } else {
                logger.info("Single deal not inserted (duplicate or error) {}", deal.getDealId());
            }

        } catch (Exception e) {
            logger.error("Failed to import line: {}: {}", line, e.getMessage(), e);
        }
    }

    public void importSingleLine(String line) {
        if (line == null || line.isBlank()) {
            logger.warn("Skipped empty or null line in importSingleLine");
            return;
        }

        try {
            Deal deal = parser.parseLine(line);
            if (validator.isValid(deal)) {
                repository.insertDeal(deal);
                logger.info("Inserted single parsed deal {}", deal.getDealId());
            } else {
                logger.warn("Invalid deal format, skipping: {}", line);
            }
        } catch (Exception e) {
            logger.error("Failed to import single line {}: {}", line, e.getMessage(), e);
        }
    }
}
