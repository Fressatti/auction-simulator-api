package com.example.auction.adapter.in.web.mapper;

import com.example.auction.adapter.in.web.dto.CreateSimulationRequest;
import com.example.auction.adapter.in.web.dto.SimulationResponse;
import com.example.auction.application.port.in.command.CreateSimulationCommand;
import com.example.auction.domain.model.Simulation;
import org.springframework.stereotype.Component;

/** Converts transport DTOs at the web boundary, keeping them outside the application core. */
@Component
public class SimulationApiMapper {
    public CreateSimulationCommand toCommand(CreateSimulationRequest request) {
        final var vehicle = request.getVehicle();
        final var values = request.getValues();
        return new CreateSimulationCommand(
                new com.example.auction.domain.model.Vehicle(
                        vehicle.getMake(), vehicle.getModel(), vehicle.getColor(),
                        vehicle.getAuctionDate(), vehicle.getAuctioneer(),
                        com.example.auction.domain.model.DamageClassification.valueOf(
                                vehicle.getDamageClassification().getValue())),
                new com.example.auction.domain.model.SimulationValues(
                        values.getBidAmount(), values.getCommissionPercentage(),
                        values.getOtherCosts(), values.getEstimatedResalePrice())
        );
    }

    public SimulationResponse toResponse(Simulation simulation) {
        final var result = simulation.result();

        final var responseResult =
                new com.example.auction.adapter.in.web.dto.SimulationResult();

        responseResult.setCommissionAmount(result.commissionAmount());
        responseResult.setTotalCost(result.totalCost());
        responseResult.setEstimatedProfit(result.estimatedProfit());
        responseResult.setProfitMarginPercentage(
                result.profitMarginPercentage()
        );

        final var response = new SimulationResponse();
        response.setId(simulation.id());
        response.setResult(responseResult);

        return response;
    }
}
