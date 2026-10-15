package com.example.auction.application.service;

import com.example.auction.application.port.in.CreateSimulationUseCase;
import com.example.auction.application.port.in.command.CreateSimulationCommand;
import com.example.auction.application.port.out.SimulationRepository;
import com.example.auction.domain.model.Simulation;
import com.example.auction.domain.service.SimulationCalculator;

public class CreateSimulationService implements CreateSimulationUseCase {
    private final SimulationCalculator calculator;
    private final SimulationRepository repository;

    public CreateSimulationService(SimulationCalculator calculator, SimulationRepository repository) {
        this.calculator = calculator;
        this.repository = repository;
    }

    @Override
    public Simulation execute(CreateSimulationCommand command) {
        // TODO: Calculate using calculator.calculate(command.values()).
        // TODO: Build Simulation with an ID, creation timestamp and calculation result.
        // TODO: Return repository.save(simulation).
        return null;
    }
}
