package com.example.kn.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
public class CurrentRateData {
    BigDecimal rate;
    LocalDateTime timeUpdated;
}
