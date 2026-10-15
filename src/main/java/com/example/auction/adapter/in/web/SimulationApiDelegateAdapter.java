package com.example.auction.adapter.in.web;

import com.example.auction.adapter.in.web.api.SimulationsApiDelegate;
import com.example.auction.adapter.in.web.dto.CreateSimulationRequest;
import com.example.auction.adapter.in.web.dto.SimulationPage;
import com.example.auction.adapter.in.web.dto.SimulationResponse;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.example.auction.adapter.in.web.mapper.SimulationApiMapper;
import com.example.auction.application.port.in.CreateSimulationUseCase;
import com.example.auction.application.port.in.command.CreateSimulationCommand;
import com.example.auction.domain.model.Simulation;

/**
 * Handwritten HTTP adapter. Replace each placeholder with a call to an application use case.
 * The generated controller delegates to this Spring bean.
 */
@Component
public class SimulationApiDelegateAdapter implements SimulationsApiDelegate {
    private final CreateSimulationUseCase createSimulationUseCase;
    private final SimulationApiMapper mapper;
    private final com.example.auction.application.port.in.GetSimulationUseCase getSimulationUseCase;

    public SimulationApiDelegateAdapter(CreateSimulationUseCase createSimulationUseCase,
                                        SimulationApiMapper mapper,
                                        com.example.auction.application.port.in.GetSimulationUseCase getSimulationUseCase) {
        this.createSimulationUseCase = createSimulationUseCase;
        this.mapper = mapper;
        this.getSimulationUseCase = getSimulationUseCase;
    }

    @Override
    public ResponseEntity<SimulationResponse> createSimulation(CreateSimulationRequest request) {
        final var command = mapper.toCommand(request);
        final var simulation = createSimulationUseCase.execute(command);
        final var response = mapper.toResponse(simulation);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<SimulationPage> listSimulations(Integer page, Integer size) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<SimulationResponse> getSimulationById(UUID id) {
        return ResponseEntity.ok(mapper.toResponse(getSimulationUseCase.execute(id)));
    }

    @Override
    public ResponseEntity<SimulationResponse> updateSimulation(UUID id, CreateSimulationRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<Void> deleteSimulation(UUID id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
