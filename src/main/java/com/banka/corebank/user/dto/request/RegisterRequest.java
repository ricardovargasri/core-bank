package com.banka.corebank.user.dto.request;

public record RegisterRequest(
        String email,
        String password,
        String firstName,
        String lastName,
        String documentId) {
}
