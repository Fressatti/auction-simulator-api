package com.example.auction.application.port.out;

import com.example.auction.domain.model.Simulation;

/** An application need, independent of any database technology. */
public interface SimulationRepository {
    Simulation save(Simulation simulation);
}
