package com.example.auction.adapter.out.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SimulationJpaRepository extends JpaRepository<SimulationEntity, UUID> {}
