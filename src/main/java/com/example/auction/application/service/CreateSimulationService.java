package com.example.auction.application.service;

import com.example.auction.application.port.in.CreateSimulationUseCase;
import com.example.auction.application.port.in.command.CreateSimulationCommand;
import com.example.auction.application.port.out.SimulationRepository;
import com.example.auction.domain.model.Simulation;
import com.example.auction.domain.service.SimulationCalculator;

public class CreateSimulationService implements CreateSimulationUseCase {

    private final SimulationCalculator calculator;
    private final SimulationRepository repository;

    public CreateSimulationService(
            SimulationCalculator calculator,
            SimulationRepository repository) {
        this.calculator = calculator;
        this.repository = repository;
    }

    @Override
    public Simulation execute(CreateSimulationCommand command) {
        final var result = calculator.calculate(command.values());

        final var simulation = new Simulation(
                java.util.UUID.randomUUID(),
                command.vehicle(),
                command.values(),
                result,
                java.time.Instant.now()
        );
        return repository.save(simulation);
    }
}
