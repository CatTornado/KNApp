package com.example.kn.service;

import com.example.kn.model.CurrencyData;
import com.example.kn.model.CurrentRateData;
import com.example.kn.properties.ApplicationProperties;
import com.example.kn.web.RestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.util.*;

import static java.math.RoundingMode.HALF_EVEN;
import static java.text.MessageFormat.format;

@Slf4j
@Service
public class DataAggregationService {

    private final RestService restService;
    private final ApplicationProperties properties;
    private final int scale;

    public DataAggregationService(@Autowired RestService restService,
                                  @Autowired ApplicationProperties properties) {
        this.restService = restService;
        this.properties = properties;
        this.scale = properties.getScale();
    }

    public Map<String, CurrencyData> collectData() throws Exception {
        log.info(format("Starting data collection. Will try to collect data for the following currencies: {0}",
                properties.getCurrencyList()));
        long startTime = System.currentTimeMillis();

        Map<String, CurrencyData> currencyDataMap = new HashMap<>();

        for (String currency : properties.getCurrencyList()) {
            try {
                CurrentRateData currentRateData = restService.getCurrentRate(currency);

                List<BigDecimal> rateStatistics = restService.getRateStatistics(currency);

                Optional<BigDecimal> minRate = rateStatistics.stream().unordered()
                        .min(Comparator.naturalOrder())
                        .map(rate -> rate.setScale(scale, HALF_EVEN));
                Optional<BigDecimal> maxRate = rateStatistics.stream().unordered()
                        .max(Comparator.naturalOrder())
                        .map(rate -> rate.setScale(scale, HALF_EVEN));
                Optional<BigDecimal> avgRate = rateStatistics.size() == 0 ? Optional.empty()
                        : Optional.of(rateStatistics.stream().unordered()
                            .reduce(BigDecimal.ZERO, BigDecimal::add, BigDecimal::add)
                            .divide(BigDecimal.valueOf(rateStatistics.size()), scale, HALF_EVEN));

                currencyDataMap.put(currency, new CurrencyData(currency,
                        currentRateData.getRate().setScale(scale, HALF_EVEN),
                        currentRateData.getTimeUpdated(), properties.getStatisticsInterval(),
                        minRate.orElse(null), maxRate.orElse(null), avgRate.orElse(null)));
            } catch (HttpClientErrorException.NotFound e) {
                log.warn(format("Currency {0} configured in application.properties isn't supported " +
                        "by the CoinDesk API. Consider fixing the application.properties file", currency));
            } catch (RuntimeException e) {
                log.error(format("Exception while collecting data for currency {0}: " + e, currency));
            }
        }

        log.info(format("Finished data collection in {0} ms. Collected data for the following currencies: {1}",
                System.currentTimeMillis() - startTime, currencyDataMap.keySet()));
        return currencyDataMap;
    }

}
