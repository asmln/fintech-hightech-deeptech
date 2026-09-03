package com.github.asmln.fintech_app.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionEvent(
        UUID transactionId,
        UUID externalId,
        UUID userId,
        BigDecimal amount,
        BigDecimal balance,
        TransactionType transactionType,
        Instant createdAt
) {}
