package com.example.kn.unit.console;

import com.example.kn.console.ConsoleComponent;
import com.example.kn.model.CurrencyData;
import com.example.kn.service.DataAggregationService;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.mockito.Mockito.*;

public class ConsoleComponentTests {

    @SuppressWarnings("unchecked")
    private final Map<String, CurrencyData> currencyDataMap = mock(Map.class);
    private final DataAggregationService dataAggregationService = mock(DataAggregationService.class);
    private final ConsoleComponent consoleComponent =  new ConsoleComponent(dataAggregationService);

    @Test
    public void testRefresh() throws Exception {
        when(dataAggregationService.collectData()).thenReturn(currencyDataMap);

        consoleComponent.refresh();

        verify(dataAggregationService, times(1)).collectData();
    }

    @Test
    public void testStats() throws Exception {
        when(dataAggregationService.collectData()).thenReturn(currencyDataMap);
        consoleComponent.refresh();

        consoleComponent.stats("EUR");

        verify(currencyDataMap, times(1)).get(anyString());
    }
}
