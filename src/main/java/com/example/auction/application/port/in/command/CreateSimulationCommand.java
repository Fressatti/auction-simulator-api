package com.example.auction.application.port.in.command;

import com.example.auction.domain.model.Vehicle;
import com.example.auction.domain.model.SimulationValues;

public record CreateSimulationCommand(Vehicle vehicle, SimulationValues values) {}
