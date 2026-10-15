package com.example.auction.application.port.in.command;

import com.example.auction.domain.model.Vehicle;
import com.example.auction.domain.model.SimulationValues;

/** Application input, independent of generated OpenAPI DTOs. */
public record CreateSimulationCommand(Vehicle vehicle, SimulationValues values) {}
