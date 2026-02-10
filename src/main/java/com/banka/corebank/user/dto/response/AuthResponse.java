package com.banka.corebank.user.dto.response;

import com.banka.corebank.user.enums.UserRole;

public record AuthResponse(
                String token,
                String refreshToken,
                String email,
                String firstName,
                UserRole role) {
}
