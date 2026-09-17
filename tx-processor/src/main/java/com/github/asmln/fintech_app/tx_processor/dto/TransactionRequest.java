package com.github.asmln.fintech_app.tx_processor.dto;

import com.github.asmln.fintech_app.domain.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

// spotless:off
public record TransactionRequest(
        @NotNull(message = "ID пользователя не может быть null")
        UUID userId,
        @NotNull(message = "Внешний ID транзакции не может быть null")
        UUID externalId,
        @NotNull(message = "Сумма не может быть null")
        @DecimalMin(value = "0.01", message = "Минимальная сумма транзакции — 0.01")
        @Digits(integer = 10, fraction = 2, message = "Разрешено не более 2 знаков после запятой")
        BigDecimal amount,
        @NotNull(message = "Тип транзакции обязателен (DEPOSIT/WITHDRAWAL)")
        TransactionType type
) {}
// spotless:on
