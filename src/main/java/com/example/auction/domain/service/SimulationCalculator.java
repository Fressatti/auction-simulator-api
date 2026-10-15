package com.example.auction.domain.service;

import com.example.auction.domain.model.SimulationResult;
import com.example.auction.domain.model.SimulationValues;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SimulationCalculator {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    public SimulationResult calculate(SimulationValues values) {
        final var bidAmount = values.bidAmount();
        final var commissionPercentage = values.commissionPercentage();
        final var otherCosts = values.otherCosts();
        final var estimatedResalePrice = values.estimatedResalePrice();

        if (estimatedResalePrice.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Estimated resale price must be greater than zero."
            );
        }

        final var commissionAmount =
                calculateCommissionAmount(bidAmount, commissionPercentage);

        final var totalCost = bidAmount
                .add(commissionAmount)
                .add(otherCosts)
                .setScale(SCALE, ROUNDING_MODE);

        final var estimatedProfit = estimatedResalePrice
                .subtract(totalCost)
                .setScale(SCALE, ROUNDING_MODE);

        final var profitMarginPercentage = estimatedProfit
                .multiply(ONE_HUNDRED)
                .divide(estimatedResalePrice, SCALE, ROUNDING_MODE);

        return new SimulationResult(
                commissionAmount,
                totalCost,
                estimatedProfit,
                profitMarginPercentage
        );
    }

    private BigDecimal calculateCommissionAmount(
            BigDecimal bidAmount,
            BigDecimal commissionPercentage) {

        return bidAmount
                .multiply(commissionPercentage)
                .divide(ONE_HUNDRED, SCALE, ROUNDING_MODE);
    }
}