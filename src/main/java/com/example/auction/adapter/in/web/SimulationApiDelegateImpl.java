package com.example.auction.adapter.in.web;

import com.example.auction.adapter.in.web.api.SimulationsApiDelegate;
import com.example.auction.adapter.in.web.dto.CreateSimulationRequest;
import com.example.auction.adapter.in.web.dto.SimulationPage;
import com.example.auction.adapter.in.web.dto.SimulationResponse;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Handwritten HTTP adapter. Replace each placeholder with a call to an application use case.
 * The generated controller delegates to this Spring bean.
 */
@Service
public class SimulationApiDelegateImpl implements SimulationsApiDelegate {

    @Override
    public ResponseEntity<SimulationResponse> createSimulation(CreateSimulationRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<SimulationPage> listSimulations(Integer page, Integer size) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<SimulationResponse> getSimulationById(UUID id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<SimulationResponse> updateSimulation(UUID id, CreateSimulationRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<Void> deleteSimulation(UUID id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
