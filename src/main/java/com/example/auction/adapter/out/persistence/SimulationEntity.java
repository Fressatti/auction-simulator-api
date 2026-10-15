package com.example.auction.adapter.out.persistence;

import com.example.auction.domain.model.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "simulations")
public class SimulationEntity {
    @Id
    @Column(nullable = false)
    private UUID id;
    @Column(nullable = false)
    private String make;
    @Column(nullable = false)
    private String model;
    @Column(nullable = false)
    private String color;
    @Column(nullable = false)
    private LocalDate auctionDate;
    @Column(nullable = false)
    private String auctioneer;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DamageClassification damageClassification;
    @Column(nullable = false, precision = 38, scale = 2)
    private BigDecimal bidAmount;
    @Column(nullable = false, precision = 38, scale = 2)
    private BigDecimal commissionPercentage;
    @Column(nullable = false, precision = 38, scale = 2)
    private BigDecimal otherCosts;
    @Column(nullable = false, precision = 38, scale = 2)
    private BigDecimal estimatedResalePrice;
    @Column(nullable = false, precision = 38, scale = 2)
    private BigDecimal commissionAmount;
    @Column(nullable = false, precision = 38, scale = 2)
    private BigDecimal totalCost;
    @Column(nullable = false, precision = 38, scale = 2)
    private BigDecimal estimatedProfit;
    @Column(nullable = false, precision = 38, scale = 2)
    private BigDecimal profitMarginPercentage;
    @Column(nullable = false)
    private Instant createdAt;

    protected SimulationEntity() {}

    static SimulationEntity fromDomain(Simulation simulation) {
        final var entity = new SimulationEntity();
        entity.id = simulation.id();
        entity.make = simulation.vehicle().make();
        entity.model = simulation.vehicle().model();
        entity.color = simulation.vehicle().color();
        entity.auctionDate = simulation.vehicle().auctionDate();
        entity.auctioneer = simulation.vehicle().auctioneer();
        entity.damageClassification = simulation.vehicle().damageClassification();
        entity.bidAmount = simulation.values().bidAmount();
        entity.commissionPercentage = simulation.values().commissionPercentage();
        entity.otherCosts = simulation.values().otherCosts();
        entity.estimatedResalePrice = simulation.values().estimatedResalePrice();
        entity.commissionAmount = simulation.result().commissionAmount();
        entity.totalCost = simulation.result().totalCost();
        entity.estimatedProfit = simulation.result().estimatedProfit();
        entity.profitMarginPercentage = simulation.result().profitMarginPercentage();
        entity.createdAt = simulation.createdAt();
        return entity;
    }

    Simulation toDomain() {
        return new Simulation(id,
                new Vehicle(make, model, color, auctionDate, auctioneer, damageClassification),
                new SimulationValues(bidAmount, commissionPercentage, otherCosts, estimatedResalePrice),
                new SimulationResult(commissionAmount, totalCost, estimatedProfit, profitMarginPercentage),
                createdAt);
    }
}
