package com.banka.corebank.transaction.dto.request;

import java.math.BigDecimal;

public record TransferRequest(
        String sourceAccountNumber,
        String destinationAccountNumber,
        BigDecimal amount,
        String description) {
}
