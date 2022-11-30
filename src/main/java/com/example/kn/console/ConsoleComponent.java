package com.example.kn.console;

import com.example.kn.model.CurrencyData;
import com.example.kn.service.DataAggregationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import javax.annotation.PostConstruct;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.example.kn.util.MessageConstants.*;
import static java.text.MessageFormat.format;

@ShellComponent
public class ConsoleComponent {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss, dd.MM.yyyy");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#########.######");
    private static final String STR = new String(new char[] { 47, 92, 47, 124, 40, 32, 39, 32, 118, 32, 39, 32,
            41, 124, 92, 47, 92, 58, 62, 104, 111, 111, 109, 97, 110 });

    private final DataAggregationService dataAggregationService;
    private Map<String, CurrencyData> currencyDataMap;

    public ConsoleComponent(@Autowired DataAggregationService dataAggregationService) {
        this.dataAggregationService = dataAggregationService;
    }

    @PostConstruct
    private void init() throws Exception {
        currencyDataMap = dataAggregationService.collectData();
        System.out.println(AT_STARTUP);
    }

    @ShellMethod(value = "Print currency data")
    public void stats(@ShellOption String currency) {

        CurrencyData data = currencyDataMap.get(currency.toUpperCase());

        if (data == null) {
            System.out.println(format(NO_SUCH_CURRENCY, currency));
            return;
        }

        if (data.getMax() != null && data.getMin() != null && data.getAvg() != null) {
            System.out.println(format(CURRENCY_INFO_FULL, data.getCurrency(),
                    FORMATTER.format(data.getTimeUpdated()), DECIMAL_FORMAT.format(data.getCurrentRate()),
                    data.getInterval(), DECIMAL_FORMAT.format(data.getMax()), DECIMAL_FORMAT.format(data.getMin()),
                    DECIMAL_FORMAT.format(data.getAvg())));
        } else {
            System.out.println(format(CURRENCY_INFO_SHORT, data.getCurrency(),
                    FORMATTER.format(data.getTimeUpdated()), DECIMAL_FORMAT.format(data.getCurrentRate()),
                    data.getInterval()));
        }
    }

    @ShellMethod(value = "Refresh currency data")
    public void refresh() throws Exception {
        currencyDataMap = dataAggregationService.collectData();
        System.out.println(REFRESHED);
    }

    @ShellMethod()
    public void spider() {
        System.out.println(STR);
    }

}