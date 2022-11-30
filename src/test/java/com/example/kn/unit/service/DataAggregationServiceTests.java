package com.example.kn.unit.service;

import com.example.kn.model.CurrencyData;
import com.example.kn.model.CurrentRateData;
import com.example.kn.properties.ApplicationProperties;
import com.example.kn.service.DataAggregationService;
import com.example.kn.web.RestService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.math.RoundingMode.HALF_EVEN;
import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DataAggregationServiceTests {

    private static RestService restServiceMock;
    private static final ApplicationProperties applicationPropertiesMock = mock(ApplicationProperties.class);
    private DataAggregationService dataAggregationService;

    private static final List<String> currencyValues = List.of("EUR", "USD", "JPY");
    private static final int scale = 1;
    private static final CurrentRateData currentRateForTest = new CurrentRateData(new BigDecimal("2.123"), now());
    private static final List<BigDecimal> rateStatisticsForTest = IntStream.rangeClosed(1, 10)
            .mapToObj(i -> new BigDecimal(i).setScale(scale, HALF_EVEN)).collect(Collectors.toList());
    private static final BigDecimal avgForTest = rateStatisticsForTest.stream()
                            .reduce(BigDecimal.ZERO, BigDecimal::add, BigDecimal::add)
                            .divide(new BigDecimal(rateStatisticsForTest.size()), scale, HALF_EVEN);

    @BeforeAll
    private static void initAll() {
        when(applicationPropertiesMock.getCurrencyList()).thenReturn(currencyValues);
        when(applicationPropertiesMock.getScale()).thenReturn(scale);
    }

    @BeforeEach
    private void initEach() {
        restServiceMock = mock(RestService.class);
        dataAggregationService = new DataAggregationService(restServiceMock, applicationPropertiesMock);
    }

    @Test
    public void testDataCollectionPositive() throws Exception {
        when(restServiceMock.getCurrentRate(anyString())).thenReturn(currentRateForTest);
        when(restServiceMock.getRateStatistics(anyString())).thenReturn(rateStatisticsForTest);

        Map<String, CurrencyData> currencyDataMap = dataAggregationService.collectData();
        assertEquals(currencyValues.size(), currencyDataMap.size());

        final CurrencyData currencyData = currencyDataMap.get(currencyValues.get(0));
        assertEquals(currencyValues.get(0), currencyData.getCurrency());
        assertEquals(currentRateForTest.getRate().setScale(scale, HALF_EVEN), currencyData.getCurrentRate());
        assertEquals(rateStatisticsForTest.get(0), currencyData.getMin());
        assertEquals(rateStatisticsForTest.get(9), currencyData.getMax());
        assertEquals(avgForTest, currencyData.getAvg());
    }

    @Test
    public void testDataCollectionCurrencyMissing() throws Exception {
        when(restServiceMock.getCurrentRate(anyString())).thenThrow(HttpClientErrorException.NotFound.class)
                .thenReturn(currentRateForTest).thenThrow(NullPointerException.class);
        when(restServiceMock.getRateStatistics(anyString())).thenReturn(List.of());

        Map<String, CurrencyData> currencyDataMap = dataAggregationService.collectData();
        assertEquals(1, currencyDataMap.size());
    }

    @Test
    public void testDataCollectionNoStatisticsAvailable() throws Exception {
        when(restServiceMock.getCurrentRate(anyString())).thenReturn(currentRateForTest);
        when(restServiceMock.getRateStatistics(anyString())).thenReturn(List.of());

        Map<String, CurrencyData> currencyDataMap = dataAggregationService.collectData();
        assertEquals(currencyValues.size(), currencyDataMap.size());

        final CurrencyData currencyData = currencyDataMap.get(currencyValues.get(0));
        assertEquals(currencyValues.get(0), currencyData.getCurrency());
        assertEquals(currentRateForTest.getRate().setScale(scale, HALF_EVEN), currencyData.getCurrentRate());
        assertNull(currencyData.getMin());
        assertNull(currencyData.getMax());
        assertNull(currencyData.getAvg());
    }
}
