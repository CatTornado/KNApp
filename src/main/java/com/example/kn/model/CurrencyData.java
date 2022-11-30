package com.example.kn.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
public class CurrencyData {
    private String currency;
    private BigDecimal currentRate;
    private LocalDateTime timeUpdated;
    private int interval;
    private BigDecimal min;
    private BigDecimal max;
    private BigDecimal avg;
}
