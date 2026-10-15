package com.example.auction.adapter.in.web;

import java.util.List;
import java.util.Objects;
import org.slf4j.MDC;

public record ApiError(String code, String message, List<FieldViolation> fields, String requestId) {
    static ApiError of(String code, String message, List<FieldViolation> fields) {
        return new ApiError(code, message, List.copyOf(fields), Objects.toString(MDC.get("requestId"), ""));
    }

    public record FieldViolation(String field, String code, String message) {}
}
