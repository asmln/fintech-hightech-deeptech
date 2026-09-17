package com.github.asmln.fintech_app.tx_processor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(
        TransactionStatus status,
        String message,
        UUID externalId,
        UUID userId,
        Instant createdAt,
        @JsonInclude(JsonInclude.Include.NON_DEFAULT) boolean duplicate
) {
    public TransactionResponse(
            TransactionStatus status,
            String message,
            UUID externalId,
            UUID userId,
            Instant createdAt
    ) {
        this(status, message, externalId, userId, createdAt, false);
    }
}
