package com.example.auction.domain.model;

import java.math.BigDecimal;

public record SimulationResult(
    BigDecimal commissionAmount,
    BigDecimal totalCost,
    BigDecimal estimatedProfit,
    BigDecimal profitMarginPercentage
) {}
