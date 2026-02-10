package com.banka.corebank.transaction.dto.response;

import com.banka.corebank.transaction.enums.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        BigDecimal amount,
        TransactionType type,
        String description,
        Instant createdAt,
        String accountNumber,
        BigDecimal newBalance) {
}
