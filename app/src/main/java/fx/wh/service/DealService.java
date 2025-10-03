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

}
