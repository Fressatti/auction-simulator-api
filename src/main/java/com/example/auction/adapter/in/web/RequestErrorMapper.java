package com.example.auction.adapter.in.web;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.validation.FieldError;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.ValueInstantiationException;

final class RequestErrorMapper {
    private RequestErrorMapper() {}

    static ApiError.FieldViolation validationField(FieldError error) {
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
        return new ApiError.FieldViolation(error.getField(), code, message);
    }


    static ApiError unreadable(Throwable exception) {
        for (Throwable current = exception; current != null; current = current.getCause()) {
            if (current instanceof InvalidFormatException invalid) {
                var type = invalid.getTargetType();
                var isEnum = type != null && type.isEnum();
                var message = isEnum
                        ? "Valor inválido. Valores aceitos: " + Arrays.toString(type.getEnumConstants())
                        : "O valor informado possui formato ou tipo inválido.";
                return ApiError.of(isEnum ? "INVALID_ENUM" : "INVALID_FIELD",
                        "Existem campos inválidos na requisição.",
                        List.of(new ApiError.FieldViolation(
                                invalid.getPath().stream()
                                        .map(ref -> ref.getPropertyName() == null ? "[" + ref.getIndex() + "]" : ref.getPropertyName())
                                        .collect(Collectors.joining(".")),
                                isEnum ? "INVALID_ENUM" : "INVALID_FORMAT", message)));
            }
            if (current instanceof ValueInstantiationException invalid && invalid.getType().isEnumType()) {
                return ApiError.of("INVALID_ENUM", "Existem campos inválidos na requisição.",
                        List.of(new ApiError.FieldViolation(
                                invalid.getPath().stream()
                                        .map(ref -> ref.getPropertyName() == null ? "[" + ref.getIndex() + "]" : ref.getPropertyName())
                                        .collect(Collectors.joining(".")),
                                "INVALID_ENUM", "Valor inválido. Valores aceitos: "
                                        + Arrays.toString(invalid.getType().getRawClass().getEnumConstants()))));
            }
        }
        return ApiError.of("INVALID_JSON",
                "Não foi possível ler o corpo da requisição. Verifique o JSON e os tipos dos campos.", List.of());
    }
}
