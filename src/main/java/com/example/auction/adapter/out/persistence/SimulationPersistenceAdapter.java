package com.example.auction.adapter.out.persistence;

import com.example.auction.application.port.out.SimulationRepository;
import com.example.auction.domain.model.Simulation;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class SimulationPersistenceAdapter implements SimulationRepository {
    private final SimulationJpaRepository repository;

    public SimulationPersistenceAdapter(SimulationJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Optional<Simulation> findById(java.util.UUID id) {
        return repository.findById(id).map(SimulationEntity::toDomain);
    }

    @Override
    @Transactional
    public Simulation save(Simulation simulation) {
        return repository.saveAndFlush(SimulationEntity.fromDomain(simulation)).toDomain();
    }
}
