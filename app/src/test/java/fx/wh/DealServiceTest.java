package fx.wh;

import fx.wh.model.Deal;
import fx.wh.repository.DealRepository;
import fx.wh.service.DealService;
import fx.wh.utils.Parser;
import fx.wh.utils.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.*;

class DealServiceTest {

    private Parser parser;
    private Validator validator;
    private DealRepository repository;
    private DealService service;

    @BeforeEach
    void setUp() {
        parser = mock(Parser.class);
        validator = mock(Validator.class);
        repository = mock(DealRepository.class);
        service = new DealService(parser, validator, repository);
    }


    @Test
    void processFileWithParseErrors() throws Exception {
        Deal deal = new Deal("D2", "USD", "EUR", Instant.parse("2025-10-04T15:07:16Z"), BigDecimal.TEN);
        Parser.Result result = new Parser.Result(1, deal, List.of("parse error")); // non-empty errors

        when(parser.parse(any(File.class))).thenReturn(List.of(result));

        service.processFile(new File("dummy.csv"));

        verify(repository, never()).insertDeal(any());
    }

    @Test
    void processFileWithValidationErrors() throws Exception {
        Deal deal = new Deal("D3", "USD", "EUR", Instant.parse("2025-10-04T15:07:16Z"), BigDecimal.TEN);
        Parser.Result result = new Parser.Result(1, deal, List.of());

        when(parser.parse(any(File.class))).thenReturn(List.of(result));
        when(validator.validate(deal)).thenReturn(new Validator.Result(false, List.of("Invalid")));

        service.processFile(new File("dummy.csv"));

        verify(repository, never()).insertDeal(any());
    }

    @Test
    void importSingleLineValidDeal() throws Exception {
        Deal deal = new Deal("D4", "USD", "EUR", Instant.parse("2025-10-04T15:07:16Z"), BigDecimal.TEN);

        when(parser.parseLine(anyString())).thenReturn(deal);
        when(validator.isValid(deal)).thenReturn(true);
        when(repository.insertDeal(deal)).thenReturn(true);

        service.importSingleLine("D4,USD,EUR,2025-10-04T15:07:16Z,10");

        verify(repository, times(1)).insertDeal(deal);
    }

    @Test
    void importSingleLineInvalidDeal() throws Exception {
        Deal deal = new Deal("D5", "USD", "EUR", Instant.parse("2025-10-04T15:07:16Z"), BigDecimal.TEN);

        when(parser.parseLine(anyString())).thenReturn(deal);
        when(validator.isValid(deal)).thenReturn(false);

        service.importSingleLine("D5,USD,EUR,2025-10-04T15:07:16Z,10");

        verify(repository, never()).insertDeal(any());
    }
}
