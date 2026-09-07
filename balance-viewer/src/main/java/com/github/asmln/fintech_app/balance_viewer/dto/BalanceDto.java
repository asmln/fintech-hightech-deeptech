package com.github.asmln.fintech_app.balance_viewer.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BalanceDto(UUID userId, BigDecimal balance, Instant updatedAt) {}
