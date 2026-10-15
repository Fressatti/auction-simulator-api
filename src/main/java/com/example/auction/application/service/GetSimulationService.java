package com.example.auction.application.service;

import com.example.auction.application.port.in.GetSimulationUseCase;
import com.example.auction.application.port.out.SimulationRepository;
import com.example.auction.application.exception.SimulationNotFoundException;
import com.example.auction.domain.model.Simulation;
import java.util.UUID;

public class GetSimulationService implements GetSimulationUseCase {
    private final SimulationRepository repository;

    public GetSimulationService(SimulationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Simulation execute(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new SimulationNotFoundException(id));
    }
}
