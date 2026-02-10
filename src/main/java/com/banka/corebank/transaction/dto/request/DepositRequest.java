package com.banka.corebank.transaction.dto.request;

import java.math.BigDecimal;

public record DepositRequest(
        String accountNumber,
        BigDecimal amount,
        String description) {
}
