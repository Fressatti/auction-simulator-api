package com.example.auction;

import com.example.auction.application.port.in.CreateSimulationUseCase;
import com.example.auction.application.port.in.command.CreateSimulationCommand;
import com.example.auction.domain.model.*;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.*;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:simulation-lookup;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class GetSimulationHttpIntegrationTests {
    @Value("${local.server.port}") int port;
    @Autowired CreateSimulationUseCase create;

    private HttpResponse<String> get(UUID id) throws Exception {
        try (var client = HttpClient.newHttpClient()) {
            return client.send(HttpRequest.newBuilder(
                    URI.create("http://localhost:" + port + "/simulations/" + id))
                    .GET().build(), HttpResponse.BodyHandlers.ofString());
        }
    }

    @Test
    void returnsSavedSimulationOverHttp() throws Exception {
        var simulation = create.execute(new CreateSimulationCommand(
                new Vehicle("Peugeot", "207", "Red", LocalDate.of(2026, 10, 14),
                        "Example", DamageClassification.MINOR_DAMAGE),
                new SimulationValues(new BigDecimal("10000.00"), new BigDecimal("5.00"),
                        new BigDecimal("1500.00"), new BigDecimal("15000.00"))));
        var response = get(simulation.id());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains(simulation.id().toString()));
        assertTrue(response.body().contains("\"result\""));
        assertTrue(response.body().contains("\"estimatedProfit\""));
        assertFalse(response.body().contains("\"vehicle\""));
        assertFalse(response.body().contains("\"values\""));
    }

    @Test
    void returnsContractErrorWhenIdDoesNotExist() throws Exception {
        var id = UUID.randomUUID();
        var response = get(id);
        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("SIMULATION_NOT_FOUND"));
        assertTrue(response.body().contains(id.toString()));
        assertTrue(response.body().contains("\"message\""));
        assertTrue(response.body().contains("\"fields\""));
    }

    private HttpResponse<String> post(String body) throws Exception {
        try (var client = HttpClient.newHttpClient()) {
            return client.send(HttpRequest.newBuilder(
                    URI.create("http://localhost:" + port + "/simulations"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body)).build(),
                    HttpResponse.BodyHandlers.ofString());
        }
    }

    @Test
    void missingFieldsReturnValidationErrorAndRequestId() throws Exception {
        var response = post("{}");
        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("VALIDATION_ERROR"));
        assertTrue(response.body().contains("\"field\""));
        assertTrue(response.headers().firstValue("X-Request-ID").isPresent());
        UUID.fromString(response.headers().firstValue("X-Request-ID").orElseThrow());
    }

    @Test
    void malformedJsonReturnsReadableError() throws Exception {
        var response = post("{");
        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("INVALID_JSON"));
        assertTrue(response.headers().firstValue("X-Request-ID").isPresent());
    }

    @Test
    void invalidUuidReturnsParameterError() throws Exception {
        try (var client = HttpClient.newHttpClient()) {
            var response = client.send(HttpRequest.newBuilder(
                    URI.create("http://localhost:" + port + "/simulations/not-a-uuid"))
                    .GET().build(), HttpResponse.BodyHandlers.ofString());
            assertEquals(400, response.statusCode());
            assertTrue(response.body().contains("INVALID_PARAMETER"));
        }
    }
}
