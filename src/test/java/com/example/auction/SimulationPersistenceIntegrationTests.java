package com.example.auction;

import com.example.auction.adapter.in.web.SimulationApiDelegateAdapter;
import com.example.auction.adapter.in.web.dto.CreateSimulationRequest;
import com.example.auction.adapter.in.web.dto.SimulationValues;
import com.example.auction.adapter.in.web.dto.Vehicle;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:simulation-inserts;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class SimulationPersistenceIntegrationTests {
    @Autowired SimulationApiDelegateAdapter delegate;
    @Autowired JdbcTemplate jdbc;

    @Test
    void createCalculatesPersistsAndReturnsOnlyIdAndResult() {
        var vehicle = new Vehicle();
        vehicle.setMake("Peugeot");
        vehicle.setModel("207");
        vehicle.setColor("Red");
        vehicle.setAuctionDate(java.time.LocalDate.of(2026, 10, 14));
        vehicle.setAuctioneer("Example");
        vehicle.setDamageClassification(Vehicle.DamageClassificationEnum.MINOR_DAMAGE);
        var values = new SimulationValues();
        values.setBidAmount(new BigDecimal("10000.00"));
        values.setCommissionPercentage(new BigDecimal("5.00"));
        values.setOtherCosts(new BigDecimal("1500.00"));
        values.setEstimatedResalePrice(new BigDecimal("15000.00"));
        var request = new CreateSimulationRequest();
        request.setVehicle(vehicle);
        request.setValues(values);

        var response = delegate.createSimulation(request);
        assertEquals(201, response.getStatusCode().value());
        var body = response.getBody();
        assertNotNull(body);
        assertNotNull(body.getId());
        assertEquals(new BigDecimal("500.00"), body.getResult().getCommissionAmount());
        assertEquals(new BigDecimal("12000.00"), body.getResult().getTotalCost());
        assertEquals(new BigDecimal("3000.00"), body.getResult().getEstimatedProfit());
        assertEquals(new BigDecimal("20.00"), body.getResult().getProfitMarginPercentage());

        var row = jdbc.queryForMap(
                "select make, total_cost, estimated_profit from simulations where id = ?",
                body.getId());
        assertEquals("Peugeot", row.get("MAKE"));
        assertEquals(new BigDecimal("12000.00"), row.get("TOTAL_COST"));
        assertEquals(new BigDecimal("3000.00"), row.get("ESTIMATED_PROFIT"));
    }
}
