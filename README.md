# auction-simulator-api

REST API for vehicle auction purchase and resale simulations.

## Stack

Java 17, Spring Boot 4.1.1, Maven Wrapper, Spring Web, Validation and Actuator.

## Run

Linux/macOS: `./mvnw spring-boot:run`

Windows PowerShell: `.\mvnw.cmd spring-boot:run`

Health check: `GET http://localhost:8080/actuator/health`.

Tests: `./mvnw test` (Windows: `.\mvnw.cmd test`).

## Architecture

- domain: business models and rules, independent of Spring.
- application/port/in: use case contracts.
- application/port/out: external dependency contracts.
- application/service: use case implementations.
- adapter/in/web: HTTP controllers and DTOs.
- adapter/out/persistence: storage implementations.
- config: dependency wiring.

This is a starter skeleton. Simulation endpoints, calculation rules, persistence and OpenAPI code generation are not implemented yet. Package documentation marks the intended boundaries.

## Conventions

All source code, identifiers, API contracts, error messages, documentation and commit messages must be written in English.

## Verification

The initial build could not resolve dependencies because Maven Central was unreachable in the preparation environment. Run the test command locally before starting implementation.
