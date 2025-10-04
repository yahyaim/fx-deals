package fx.wh;

import fx.wh.database.DatabaseConfig;
import fx.wh.repository.DealRepository;
import fx.wh.service.DealService;
import fx.wh.utils.Parser;
import fx.wh.utils.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public String getGreeting() {
        return "Welcome to FX Data Warehouse Management System!";
    }

    public static void main(String[] args) {
        App app = new App();
        logger.info(app.getGreeting());

        if (args.length < 1) {
            logger.error("Usage: java -jar fx-wh.jar <path-to-csv|single-deal-line>");
            System.exit(2);
        }

        var ds = DatabaseConfig.getDataSource();
        var repository = new DealRepository(ds);
        var parser = new Parser();
        var validator = new Validator();
        var service = new DealService(parser, validator, repository);

        try {
            String input = args[0];
            File file = new File(input);

            if (file.exists() && file.isFile()) {
                logger.info("Detected CSV file input: {}", file.getAbsolutePath());
                service.importFile(file);
            } else {
                logger.info("Detected single-line deal input.");
                processSingleDeal(input, service);
            }

        } catch (Exception e) {
            logger.error("Failed to process input: {}", e.getMessage(), e);
            System.exit(1);
        } finally {
            DatabaseConfig.close();
            logger.info("Database connection closed.");
        }
    }

    private static void processSingleDeal(String line, DealService service) {
        try {
            service.importSingleLine(line);
            logger.info("Successfully processed single deal: {}", line);
        } catch (Exception e) {
            logger.error("Failed to process single deal '{}': {}", line, e.getMessage(), e);
        }
    }
}
