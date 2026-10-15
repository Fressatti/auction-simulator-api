package com.example.auction.adapter.in.web;

import com.example.auction.application.exception.SimulationNotFoundException;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SimulationExceptionHandler {
    @ExceptionHandler(SimulationNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(SimulationNotFoundException exception) {
        return ResponseEntity.status(404).body(Map.of(
                "code", "SIMULATION_NOT_FOUND",
                "message", exception.getMessage(),
                "fields", List.of()
        ));
    }
}
