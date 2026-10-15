package com.example.auction.adapter.in.web;

import com.example.auction.application.exception.SimulationNotFoundException;
import java.util.List;
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
                .map(error -> Map.of("field", error.getField(),
                        "message", error.getDefaultMessage() == null
                                ? "Invalid value." : error.getDefaultMessage()))
                .toList();
        log.warn("Request validation failed requestId={} fields={}",
                MDC.get("requestId"), fields);
        return error(400, "VALIDATION_ERROR", "Request validation failed.", fields);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadable(HttpMessageNotReadableException exception) {
        final var cause = exception.getMostSpecificCause();
        log.warn("Request JSON could not be read requestId={} causeType={}",
                MDC.get("requestId"), cause.getClass().getSimpleName());
        return error(400, "INVALID_JSON",
                "Request body is missing, malformed, or contains an invalid field type or enum value.",
                List.of());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        log.warn("Request parameter type mismatch requestId={} parameter={}",
                MDC.get("requestId"), exception.getName());
        return error(400, "INVALID_PARAMETER", "Invalid request parameter.",
                List.of(Map.of("field", exception.getName(), "message", "Invalid format.")));
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

    private ResponseEntity<Map<String, Object>> error(
            int status, String code, String message, List<?> fields) {
        return ResponseEntity.status(status).body(Map.of(
                "code", code, "message", message, "fields", fields));
    }
}
