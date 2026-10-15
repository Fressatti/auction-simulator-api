package com.example.auction.domain.model;

import java.time.Instant;
import java.util.UUID;

public record Simulation(
    UUID id, Vehicle vehicle, SimulationValues values,
    SimulationResult result, Instant createdAt
) {}
