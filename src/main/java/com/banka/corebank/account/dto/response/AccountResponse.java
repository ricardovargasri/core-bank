package com.banka.corebank.account.dto.response;

import com.banka.corebank.account.enums.AccountType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String accountNumber,
        AccountType type,
        BigDecimal balance,
        Instant createdAt) {
}
