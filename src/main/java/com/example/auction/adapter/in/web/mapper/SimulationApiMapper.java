package com.example.auction.adapter.in.web.mapper;

import com.example.auction.adapter.in.web.dto.CreateSimulationRequest;
import com.example.auction.adapter.in.web.dto.SimulationResponse;
import com.example.auction.application.port.in.command.CreateSimulationCommand;
import com.example.auction.domain.model.Simulation;
import org.springframework.stereotype.Component;

/** Converts transport DTOs at the web boundary, keeping them outside the application core. */
@Component
public class SimulationApiMapper {
    public CreateSimulationCommand toCommand(CreateSimulationRequest request) {
        // TODO: Convert generated Vehicle and SimulationValues DTOs to domain records.
        // TODO: Convert the generated damage enum to the domain DamageClassification.
        return null;
    }

    public SimulationResponse toResponse(Simulation simulation) {
        // TODO: Convert the domain model to the generated response DTO.
        return null;
    }
}
