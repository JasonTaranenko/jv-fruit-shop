package core.basesyntax;

import core.basesyntax.service.DataConverter;
import core.basesyntax.service.DataReader;
import core.basesyntax.service.DataWriter;
import core.basesyntax.service.ReportGenerator;
import core.basesyntax.service.ShopService;
import core.basesyntax.service.impl.CsvDataReaderImpl;
import core.basesyntax.service.impl.CsvDataWriterImpl;
import core.basesyntax.service.impl.DataConverterImpl;
import core.basesyntax.service.impl.ReportGeneratorImpl;
import core.basesyntax.service.impl.ShopServiceImpl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static final String PATH = "src/main/resources/balance.csv";

    public static void main(String[] args) {
        //Declare Map object "handlers" for determine operation
        Map<Operation, OperationHandler> handlers = new HashMap<>();
        handlers.put(Operation.BALANCE, new BalanceHandler());
        handlers.put(Operation.PURCHASE, new PurchaseHandler());
        handlers.put(Operation.RETURN, new ReturnHandler());
        handlers.put(Operation.SUPPLY, new SupplyHandler());

        //Read the data from source
        DataReader dataReader = new CsvDataReaderImpl();
        final List<String> inputReport
                = dataReader.getDataFromFile(PATH);

        //Convert received data from reader to particular format
        DataConverter dataConverter = new DataConverterImpl();
        List<FruitTransfer> transfers = dataConverter.convertToTransfer(inputReport);

        //Process and store the data to DB
        ShopService shopService = new ShopServiceImpl();
        shopService.process(transfers, handlers);

        //Generate a report
        ReportGenerator reportGenerator = new ReportGeneratorImpl();
        List<String> transitionalReport = reportGenerator.getReport();

        //Write report to file CSV format
        DataWriter fileWriter = new CsvDataWriterImpl();
        fileWriter.writeToFile(transitionalReport, "report");

    }
}
