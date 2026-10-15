package com.example.auction.adapter.out.persistence;

import com.example.auction.application.port.out.SimulationRepository;
import com.example.auction.domain.model.Simulation;
import org.springframework.stereotype.Repository;

/** Dummy adapter. No database, JPA entity or Spring Data repository is configured yet. */
@Repository
public class SimulationPersistenceAdapter implements SimulationRepository {
    @Override
    public Simulation save(Simulation simulation) {
        // TODO: Map the domain model to a persistence entity, save and map back.
        return null;
    }
}
