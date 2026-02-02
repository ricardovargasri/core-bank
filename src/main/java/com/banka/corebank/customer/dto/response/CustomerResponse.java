package com.banka.corebank.customer.dto.response;

import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(
                String name,
                String documentId,
                String email,
                Instant createdAt,
                Instant updatedAt) {
}
