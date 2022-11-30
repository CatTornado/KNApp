package com.example.kn.unit.web;

import com.example.kn.model.CurrentRateData;
import com.example.kn.properties.ApplicationProperties;
import com.example.kn.web.RestService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static com.example.kn.util.Loader.loadTestData;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

public class RestServiceTests {

    private static final RestTemplate restTemplateMock = mock(RestTemplate.class);
    private static final ApplicationProperties propertiesMock = mock(ApplicationProperties.class);
    private static final RestService restService = new RestService(restTemplateMock, propertiesMock);

    private static String currentPriceResponse;
    private static String historicalResponse;

    @BeforeAll
    public static void init() throws IOException {
        currentPriceResponse = loadTestData("/test_data/currentprice_response_example.json");
        historicalResponse = loadTestData("/test_data/historical_response_example.json");
        when(propertiesMock.getCurrentPriceAddress()).thenReturn("");
        when(propertiesMock.getStatisticsAddress()).thenReturn("");
    }

    @Test
    public void testGetCurrentRate() throws IOException {
        when(restTemplateMock.getForEntity(anyString(), any(), anyMap()))
                .thenReturn(new ResponseEntity<>(currentPriceResponse, OK));

        BigDecimal expectedRate = new BigDecimal("16044.9554");
        CurrentRateData actual = restService.getCurrentRate("EUR");

        assertEquals(expectedRate, actual.getRate());
        assertNotNull(actual.getTimeUpdated());
    }

    @Test
    public void testGetRateStatistics() throws IOException {
        when(restTemplateMock.getForEntity(anyString(), any(), anyMap()))
                .thenReturn(new ResponseEntity<>(historicalResponse, OK));

        List<BigDecimal> expected = List.of(new BigDecimal("24139.4648"),
                new BigDecimal("26533.576"), new BigDecimal("27001.2846"));
        List<BigDecimal> actual = restService.getRateStatistics("EUR");

        assertEquals(expected.size(), actual.size());
        assertTrue(actual.contains(expected.get(0)));
        assertTrue(actual.contains(expected.get(1)));
        assertTrue(actual.contains(expected.get(2)));
    }

}
