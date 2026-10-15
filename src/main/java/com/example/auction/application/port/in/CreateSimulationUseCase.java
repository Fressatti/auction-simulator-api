package com.example.auction.application.port.in;

import com.example.auction.application.port.in.command.CreateSimulationCommand;
import com.example.auction.domain.model.Simulation;

public interface CreateSimulationUseCase {
    Simulation execute(CreateSimulationCommand command);
}
