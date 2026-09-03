package com.github.asmln.fintech_app.tx_processor.dto;

import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(
        String status,
        String message,
        UUID externalId,
        UUID userId,
        Instant createdAt
) {}
