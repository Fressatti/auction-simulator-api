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

This is a starter skeleton. Business operations and persistence are not implemented yet. OpenAPI generation is configured. The create flow is an educational null-returning skeleton; other endpoints return HTTP 501. Package documentation marks the intended boundaries.

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
The generated controller injects the handwritten `SimulationApiDelegateAdapter` Spring bean.
Implement application use cases there; map DTOs into domain objects at this boundary.

Do not edit or commit generated sources. Regenerate them after editing the contract.
Code generation also runs before compilation during the normal Maven lifecycle.
This configuration generates server code, not Swagger UI.
The create delegate follows the adapter -> mapper -> use case -> response mapper path and returns null, as requested for the educational skeleton. The mapper, service, calculator and persistence methods also return null. This is not a usable API and does not yet fulfill its response contract. Other delegate operations still return HTTP 501.

No build or code generation was run for this configuration change, as requested.

## Create simulation skeleton

| Type | Package | Responsibility |
| --- | --- | --- |
| SimulationApiDelegateAdapter | adapter.in.web | Entry adapter; depends on the input port |
| SimulationApiMapper | adapter.in.web.mapper | Generated request -> application command; domain result -> generated response |
| CreateSimulationCommand | application.port.in.command | Input record independent of HTTP |
| CreateSimulationUseCase | application.port.in | Input port |
| CreateSimulationService | application.service | Coordinates the use case |
| SimulationCalculator | domain.service | Calculation rules placeholder |
| Simulation, Vehicle, SimulationValues, SimulationResult | domain.model | Domain records |
| DamageClassification | domain.model | Domain enum, separate from the generated enum |
| SimulationRepository | application.port.out | Output port |
| SimulationPersistenceAdapter | adapter.out.persistence | Dummy output adapter |
| SimulationConfiguration | config | Connects plain application/domain classes to Spring |

The adapter calls the input port using constructor injection. SimulationConfiguration supplies CreateSimulationService.
The service receives SimulationCalculator and SimulationRepository; its TODOs describe the calls to implement.
No JPA dependency or database has been added. Add persistence entities and Spring Data types only in the output adapter when choosing storage.

The request conversion belongs to the web adapter because it knows both the generated transport DTO and the application command.
The command belongs to the application and may reference domain types. Neither it nor the domain imports generated DTOs.
Returning null is temporary teaching scaffolding, not production behavior.

The repository POM still targets Java 17. A local change to Java 25 has not been published here; this skeleton does not overwrite that setting.
No build, tests or generation were executed for this skeleton, as requested.
