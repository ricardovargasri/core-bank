package com.banka.corebank.customer.dto.response;

import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String name,
        String documentId,
        String email,
        Instant createdAt,
        Instant updatedAt) {
}
