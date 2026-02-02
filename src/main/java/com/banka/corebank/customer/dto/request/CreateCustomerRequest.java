package com.banka.corebank.customer.dto.request;

public record CreateCustomerRequest(
        String name,
        String documentId,
        String email) {
}
