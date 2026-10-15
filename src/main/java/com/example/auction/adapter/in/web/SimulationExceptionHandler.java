package com.example.auction.adapter.in.web;

import com.example.auction.application.exception.SimulationNotFoundException;
import java.util.List;
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
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
        final var fields = exception.getBindingResult().getFieldErrors().stream()
                .map(RequestErrorMapper::validationField)
                .toList();
        log.warn("Request validation failed requestId={} fields={}",
                MDC.get("requestId"), fields);
        return error(400, "VALIDATION_ERROR", "Existem campos inválidos na requisição.", fields);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException exception) {
        final var cause = exception.getMostSpecificCause();
        log.warn("Request JSON could not be read requestId={} causeType={}",
                MDC.get("requestId"), cause.getClass().getSimpleName());
        return ResponseEntity.badRequest().body(RequestErrorMapper.unreadable(exception));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        log.warn("Request parameter type mismatch requestId={} parameter={}",
                MDC.get("requestId"), exception.getName());
        return error(400, "INVALID_PARAMETER", "Invalid request parameter.",
                List.of(new ApiError.FieldViolation(exception.getName(), "INVALID_FORMAT", "Formato inválido para o parâmetro.")));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiError> handleMethodValidation(HandlerMethodValidationException exception) {
        final var status = exception.getStatusCode().value();
        log.warn("Method validation failed requestId={} status={}", MDC.get("requestId"), status);
        return error(status, "VALIDATION_ERROR", "Request or response validation failed.", List.of());
    }

    @ExceptionHandler(SimulationNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(SimulationNotFoundException exception) {
        log.warn("Simulation not found requestId={} message={}", MDC.get("requestId"), exception.getMessage());
        return error(404, "SIMULATION_NOT_FOUND", exception.getMessage(), List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception exception) {
        if (exception instanceof org.springframework.web.ErrorResponse webError) {
            final var status = webError.getStatusCode().value();
            log.warn("HTTP framework error requestId={} status={} type={}",
                    MDC.get("requestId"), status, exception.getClass().getSimpleName());
            return error(status, "HTTP_ERROR", "HTTP request could not be processed.", List.of());
        }
        log.error("Unexpected request failure requestId=" + MDC.get("requestId"), exception);
        return error(500, "INTERNAL_ERROR", "An unexpected error occurred.", List.of());
    }

    private ResponseEntity<ApiError> error(
            int status, String code, String message, List<ApiError.FieldViolation> fields) {
        return ResponseEntity.status(status).body(ApiError.of(code, message, fields));
    }
}
