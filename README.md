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

## Local H2 configuration

The project targets Java 25. Run the Maven Wrapper with JDK 25.

The default profile is `local`, which configures Spring Data JPA and an H2
file database at `./data/auction-simulator.mv.db`. This path is relative to
the process working directory. Restart from the same directory to reuse data.

Run on Windows: `.\mvnw.cmd spring-boot:run`.
JDBC URL: `jdbc:h2:file:./data/auction-simulator`.
Username: `sa`; password: empty (local development only).

Hibernate uses `ddl-auto=update` for local development. Tables are created
only when JPA entities are added. This configuration does not implement
`SimulationRepository` or save simulations yet. The H2 browser console is
not configured.

The database files are excluded from Git. Use an explicit non-local profile
and its own database configuration for other environments.

## Simulation inserts

`POST /simulations` now maps the request, calculates results using the domain
calculator, assigns a UUID and UTC creation timestamp, and saves a row in
`simulations` through the JPA output adapter. The response contains `id` and
`result`. Vehicle, input values, results and timestamp are stored in the table.
Other HTTP operations remain unimplemented (501).

Start with JDK 25 and the local profile, then send a valid request matching
`docs/openapi.yaml`. Hibernate creates the table on startup.
The H2 configuration section above describes the initial setup; the JPA entity
and repository adapter are now implemented.

Run `./mvnw test` (Windows: `.\mvnw.cmd test`) with JDK 25.
`SimulationPersistenceIntegrationTests` exercises the create delegate through
the real mapper, calculator, service and JPA adapter, and checks the inserted
row in an isolated in-memory H2 database. It does not exercise HTTP transport.

## Get a simulation by ID

`GET /simulations/{id}` retrieves a persisted simulation and returns HTTP 200
with `id` and `result`. An unknown UUID returns HTTP 404 with
`code: SIMULATION_NOT_FOUND`, a message and an empty `fields` array.
The application service uses the repository output port; JPA stays in the adapter.

`GetSimulationHttpIntegrationTests` uses a real HTTP server on a random port
and an isolated in-memory H2 database to cover successful retrieval and 404.
List, update and delete operations still return HTTP 501.

For a manual file-persistence check, create a simulation with POST, retain its
ID, stop the application, restart from the same working directory using the
local profile, then call GET with that ID. This restart scenario is not covered
by the in-memory integration tests.
