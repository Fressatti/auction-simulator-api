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

This is a starter skeleton. Business operations and persistence are not implemented yet. OpenAPI generation is configured; endpoint placeholders return HTTP 501. Package documentation marks the intended boundaries.

## Conventions

All source code, identifiers, API contracts, error messages, documentation and commit messages must be written in English.

## Verification

The initial build could not resolve dependencies because Maven Central was unreachable in the preparation environment. Run the test command locally before starting implementation.

## Contract-first generation

Source of truth: `docs/openapi.yaml`.

Generate sources without compiling or packaging:

- Windows: `.\\mvnw.cmd generate-sources`
- Linux/macOS: `./mvnw generate-sources`

Generated output: `target/generated-sources/openapi/src/main/java`.
The plugin generates `SimulationsApi`, `SimulationsApiController`, `SimulationsApiDelegate`, DTOs and `ApiUtil`.
The generated controller injects the handwritten `SimulationApiDelegateImpl` Spring bean.
Implement application use cases there; map DTOs into domain objects at this boundary.

Do not edit or commit generated sources. Regenerate them after editing the contract.
Code generation also runs before compilation during the normal Maven lifecycle.
This configuration generates server code, not Swagger UI.
The handwritten delegate returns HTTP 501 placeholders until business operations are implemented; 501 is not a completed business response defined by the contract.

No build or code generation was run for this configuration change, as requested.
