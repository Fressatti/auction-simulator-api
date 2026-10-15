package com.example.auction;

import com.example.auction.domain.model.SimulationValues;
import com.example.auction.domain.service.SimulationCalculator;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimulationCalculatorTests {
    private final SimulationCalculator calculator = new SimulationCalculator();

    @Test
    void roundsCommissionBeforeAddingCosts() {
        var result = calculator.calculate(values("100.10", "5", "2", "150"));
        assertEquals(new BigDecimal("5.01"), result.commissionAmount());
        assertEquals(new BigDecimal("107.11"), result.totalCost());
        assertEquals(new BigDecimal("42.89"), result.estimatedProfit());
        assertEquals(new BigDecimal("28.59"), result.profitMarginPercentage());
    }

    @Test
    void preservesLossAsNegativeProfitAndMargin() {
        var result = calculator.calculate(values("100", "0", "20", "80"));
        assertEquals(new BigDecimal("-40.00"), result.estimatedProfit());
        assertEquals(new BigDecimal("-50.00"), result.profitMarginPercentage());
    }

    @Test
    void rejectsZeroOrNegativeResalePrice() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculate(values("100", "5", "0", "0")));
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculate(values("100", "5", "0", "-1")));
    }

    private SimulationValues values(String bid, String commission, String costs, String resale) {
        return new SimulationValues(new BigDecimal(bid), new BigDecimal(commission),
                new BigDecimal(costs), new BigDecimal(resale));
    }
}
