package com.example.auction.adapter.in.web;

import com.example.auction.application.exception.SimulationNotFoundException;
import java.util.List;
import org.springframework.validation.FieldError;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.ValueInstantiationException;
import java.util.Map;
import org.slf4j.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class SimulationExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(SimulationExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException exception) {
        final var fields = exception.getBindingResult().getFieldErrors().stream()
                .map(this::validationField)
                .toList();
        log.warn("Request validation failed requestId={} fields={}",
                MDC.get("requestId"), fields);
        return error(400, "VALIDATION_ERROR", "Existem campos inválidos na requisição.", fields);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadable(HttpMessageNotReadableException exception) {
        final var cause = exception.getMostSpecificCause();
        log.warn("Request JSON could not be read requestId={} causeType={}",
                MDC.get("requestId"), cause.getClass().getSimpleName());
        for (Throwable current = exception; current != null; current = current.getCause()) {
            if (current instanceof InvalidFormatException invalid) {
                final var field = invalid.getPath().stream()
                        .map(ref -> ref.getPropertyName() == null ? "[" + ref.getIndex() + "]" : ref.getPropertyName())
                        .collect(java.util.stream.Collectors.joining("."));
                final var type = invalid.getTargetType();
                final var isEnum = type != null && type.isEnum();
                final var message = isEnum
                        ? "Valor inválido. Valores aceitos: " + java.util.Arrays.toString(type.getEnumConstants())
                        : "O valor informado possui formato ou tipo inválido.";
                return error(400, isEnum ? "INVALID_ENUM" : "INVALID_FIELD",
                        "Existem campos inválidos na requisição.",
                        List.of(Map.of("field", field, "code", isEnum ? "INVALID_ENUM" : "INVALID_FORMAT",
                                "message", message)));
            }
            if (current instanceof ValueInstantiationException invalid && invalid.getType().isEnumType()) {
                final var field = invalid.getPath().stream()
                        .map(ref -> ref.getPropertyName() == null ? "[" + ref.getIndex() + "]" : ref.getPropertyName())
                        .collect(java.util.stream.Collectors.joining("."));
                return error(400, "INVALID_ENUM", "Existem campos inválidos na requisição.",
                        List.of(Map.of("field", field, "code", "INVALID_ENUM", "message",
                                "Valor inválido. Valores aceitos: "
                                        + java.util.Arrays.toString(invalid.getType().getRawClass().getEnumConstants()))));
            }
        }
        return error(400, "INVALID_JSON",
                "Não foi possível ler o corpo da requisição. Verifique o JSON e os tipos dos campos.",
                List.of());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        log.warn("Request parameter type mismatch requestId={} parameter={}",
                MDC.get("requestId"), exception.getName());
        return error(400, "INVALID_PARAMETER", "Invalid request parameter.",
                List.of(Map.of("field", exception.getName(), "code", "INVALID_FORMAT", "message", "Formato inválido para o parâmetro.")));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, Object>> handleMethodValidation(HandlerMethodValidationException exception) {
        final var status = exception.getStatusCode().value();
        log.warn("Method validation failed requestId={} status={}", MDC.get("requestId"), status);
        return error(status, "VALIDATION_ERROR", "Request or response validation failed.", List.of());
    }

    @ExceptionHandler(SimulationNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(SimulationNotFoundException exception) {
        log.warn("Simulation not found requestId={} message={}", MDC.get("requestId"), exception.getMessage());
        return error(404, "SIMULATION_NOT_FOUND", exception.getMessage(), List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception exception) {
        if (exception instanceof org.springframework.web.ErrorResponse webError) {
            final var status = webError.getStatusCode().value();
            log.warn("HTTP framework error requestId={} status={} type={}",
                    MDC.get("requestId"), status, exception.getClass().getSimpleName());
            return error(status, "HTTP_ERROR", "HTTP request could not be processed.", List.of());
        }
        log.error("Unexpected request failure requestId=" + MDC.get("requestId"), exception);
        return error(500, "INTERNAL_ERROR", "An unexpected error occurred.", List.of());
    }

    private Map<String, String> validationField(FieldError error) {
        final var rule = error.getCode() == null ? "" : error.getCode();
        final var code = switch (rule) {
            case "NotNull", "NotBlank", "NotEmpty" -> "REQUIRED";
            case "Size" -> "INVALID_LENGTH";
            case "Min", "Max", "DecimalMin", "DecimalMax", "Positive", "PositiveOrZero" -> "OUT_OF_RANGE";
            case "Pattern" -> "INVALID_FORMAT";
            default -> "INVALID_VALUE";
        };
        final var message = switch (code) {
            case "REQUIRED" -> "O campo deve ser informado e não pode estar vazio.";
            case "INVALID_LENGTH" -> "O tamanho do texto está fora dos limites permitidos.";
            case "OUT_OF_RANGE" -> "O valor está fora dos limites permitidos.";
            case "INVALID_FORMAT" -> switch (error.getField()) {
                case "vehicle.make", "vehicle.model", "vehicle.color", "vehicle.auctioneer" ->
                        "O texto deve conter pelo menos um caractere diferente de espaço.";
                default -> "O valor não corresponde ao formato permitido.";
            };
            default -> "O valor informado é inválido.";
        };
        return Map.of("field", error.getField(), "code", code, "message", message);
    }

    private ResponseEntity<Map<String, Object>> error(
            int status, String code, String message, List<?> fields) {
        return ResponseEntity.status(status).body(Map.of(
                "code", code, "message", message, "fields", fields,
                "requestId", java.util.Objects.toString(MDC.get("requestId"), "")));
    }
}
