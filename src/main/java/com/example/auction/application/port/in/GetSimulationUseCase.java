package com.example.auction.application.port.in;

import com.example.auction.domain.model.Simulation;
import java.util.UUID;

public interface GetSimulationUseCase {
    Simulation execute(UUID id);
}
