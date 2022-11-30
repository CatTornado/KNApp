package com.example.kn.properties;

import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@Configuration
@ConfigurationProperties
@Getter
@ToString
public class ApplicationProperties {

    @NotEmpty
    @Value("${kn.datasource.address.currentPrice}")
    private String currentPriceAddress;

    @NotEmpty
    @Value("${kn.datasource.address.statistics}")
    private String statisticsAddress;

    @Min(1)
    @Value("${kn.statistics.interval:365}")
    private int statisticsInterval;

    @Min(0)
    @Value("${kn.currency.scale:2}")
    private int scale;

    private List<String> currencyList;

    @Autowired
    public void setCurrencyList(@NotEmpty @Value("#{${kn.currency.list}}") List<String> currencyList) {
        this.currencyList = currencyList.stream().map(String::toUpperCase).collect(Collectors.toList());
    }

}
