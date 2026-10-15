package com.example.auction.config;

import com.example.auction.application.port.in.CreateSimulationUseCase;
import com.example.auction.application.port.out.SimulationRepository;
import com.example.auction.application.service.CreateSimulationService;
import com.example.auction.domain.service.SimulationCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimulationConfiguration {
    @Bean
    public SimulationCalculator simulationCalculator() {
        return new SimulationCalculator();
    }

    @Bean
    public CreateSimulationUseCase createSimulationUseCase(
            SimulationCalculator calculator, SimulationRepository repository) {
        return new CreateSimulationService(calculator, repository);
    }
}
