package com.example.kn.web;

import com.example.kn.model.CurrentRateData;
import com.example.kn.properties.ApplicationProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RestService {

    private static final LocalDate EARLIEST_DATE = LocalDate.of(2010, 7, 17);
    public static final String CURRENCY = "currency";
    public static final String START = "start";
    public static final String END = "end";

    private final RestTemplate restTemplate;
    private final ApplicationProperties properties;
    private final ObjectMapper objectMapper;

    private final LocalDate endDate;
    private final LocalDate startDate;

    public RestService(@Autowired RestTemplate restTemplate,
                       @Autowired ApplicationProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
        this.objectMapper = new ObjectMapper();
        endDate = LocalDate.now();
        startDate = calculateStartDate(endDate);
    }

    public CurrentRateData getCurrentRate(String currency) throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.getForEntity(properties.getCurrentPriceAddress(),
                                                                    String.class,
                                                                    Map.of(CURRENCY, currency));

        JsonNode tree = objectMapper.readTree(response.getBody());
        BigDecimal rate = new BigDecimal(tree
                .get("bpi").get(currency).get("rate_float").toString());
        LocalDateTime timeUpdated = ZonedDateTime.parse(tree.get("time").get("updatedISO").asText())
                .withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();

        return new CurrentRateData(rate, timeUpdated);
    }

    public List<BigDecimal> getRateStatistics(String currency) throws JsonProcessingException {

        ResponseEntity<String> response = restTemplate.getForEntity(properties.getStatisticsAddress(),
                                            String.class,
                                            Map.of(CURRENCY, currency, START, startDate, END, endDate));
        JsonNode bpi = objectMapper.readTree(response.getBody()).get("bpi");

        if (bpi == null) {
            return List.of();
        }
        Map<String, String> statistics = objectMapper.convertValue(bpi, new TypeReference<>(){});

        return statistics.values()
                .stream()
                .map(BigDecimal::new)
                .collect(Collectors.toList());
    }

    private LocalDate calculateStartDate(LocalDate endDate) {
        LocalDate userStartDate = endDate.minusDays(properties.getStatisticsInterval());
        if (EARLIEST_DATE.isBefore(userStartDate)) {
            return userStartDate;
        } else {
            log.warn("The CoinDesk API only covers data from 2010-07-17 onwards. " +
                    "Application will use 2010-07-17 as the start date for storing statistics.");
            return EARLIEST_DATE;
        }
    }

}
