package com.example.auction.domain.model;

import java.math.BigDecimal;

public record SimulationValues(
    BigDecimal bidAmount,
    BigDecimal commissionPercentage,
    BigDecimal otherCosts,
    BigDecimal estimatedResalePrice
) {}
