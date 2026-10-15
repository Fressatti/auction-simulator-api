package com.example.auction.application.exception;

import java.util.UUID;

public class SimulationNotFoundException extends RuntimeException {
    public SimulationNotFoundException(UUID id) {
        super("Simulation not found: " + id);
    }
}
